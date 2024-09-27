package fix

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import metaconfig.Configured
import scala.meta._
import scala.meta.inputs.Input
import scalafix.v1._

class SeparateEachFileRewrite(config: SeparateEachFileConfig) extends SyntacticRule("SeparateEachFileRewrite") {

  def this() = this(SeparateEachFileConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("SeparateEachFileRewrite")(this.config)
      .map(newConfig => new SeparateEachFileRewrite(newConfig))

  private def maxOption[A: Ordering](xs: Seq[A]): Option[A] =
    if (xs.isEmpty) None else Option(xs.max)

  override def fix(implicit doc: SyntacticDocument): Patch = {
    PartialFunction
      .condOpt(doc.input) {
        case f: Input.File =>
          f.path.toFile
        case f: Input.VirtualFile =>
          new File(f.path)
      }
      .filter { _ =>
        doc.tree.collect {
          case t: Stat.WithMods if t.parent.forall(_.is[Pkg.Body]) && t.mods.exists(_.is[Mod.Sealed]) =>
            ()
        }.isEmpty
      }
      .foreach { input =>
        val topLevelValues = doc.tree.collect {
          case t: (Stat.WithTemplate & Member & Stat.WithMods)
              if isTopLevel(t) && t.templ.inits.isEmpty && !t.is[Defn.Object] =>
            t
        }

        if (
          (topLevelValues.lengthCompare(config.limit) >= 0) && topLevelValues.forall(_.mods.forall(!_.is[Mod.Sealed]))
        ) {
          val headerLastPos = {
            maxOption(
              doc.tree.collect {
                case i: Import if isTopLevel(i) => i.pos.end
              }
            ).orElse(
              maxOption(
                doc.tree.collect { case p: Pkg =>
                  p.ref.pos.end
                }
              )
            ).getOrElse(0)
          }
          val header = doc.input.text.take(headerLastPos)

          assert(input.delete())

          val xs = doc.tree.collect {
            case t: (Stat.WithTemplate & Member) if isTopLevel(t) =>
              t
          }.groupBy(_.name.value)

          xs.foreach { case (k, v) =>
            Files.write(
              new File(
                input.getParentFile,
                s"${k}.scala"
              ).toPath,
              (header + "\n\n" + v
                .sortBy(_.getClass.getName)
                .map { x =>
                  (doc.comments.leading(x).toSeq.sortBy(_.pos.start).map(_.toString) :+ x.toString).mkString("\n")
                }
                .mkString("\n", "\n\n", "\n")).getBytes(StandardCharsets.UTF_8)
            )
          }
        }
      }

    Patch.empty
  }

  private def isTopLevel(t: Tree): Boolean = t.parent.forall(_.is[Pkg])
}
