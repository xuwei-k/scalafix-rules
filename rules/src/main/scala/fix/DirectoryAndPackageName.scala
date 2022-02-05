package fix

import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Pkg
import scala.meta.Term
import scala.meta.Tree
import scala.meta.inputs.Input
import scala.meta.inputs.Position

class DirectoryAndPackageName extends SyntacticRule("DirectoryAndPackageName") {

  override def isLinter = true

  private def getPackages(t: Tree): List[Term.Ref] = {
    t.collect { case p: Pkg => p.ref :: p.stats.flatMap(getPackages) }.flatten.distinct
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val packages = getPackages(doc.tree)

    val packageObjectOpt =
      doc.tree.collect { case x: Pkg.Object =>
        x
      }.headOption

    val scalaSourceOpt = PartialFunction.condOpt(doc.input) {
      case f: Input.VirtualFile =>
        f.path
      case f: Input.File =>
        f.path.toString
    }

    {
      for {
        path <- scalaSourceOpt
        // TODO Windows
        if !scala.util.Properties.isWin
        dirOpt = Seq(
          "/src/main/scala-2.11/",
          "/src/test/scala-2.11/",
          "/src/main/scala-2.12/",
          "/src/test/scala-2.12/",
          "/src/main/scala-2.13/",
          "/src/test/scala-2.13/",
          "/src/main/scala-2/",
          "/src/test/scala-2/",
          "/src/main/scala-3/",
          "/src/test/scala-3/",
          "/src/main/scala/",
          "/src/test/scala/"
        ).find { dir =>
          path.contains(dir)
        }.map { dir =>
          path.split(dir).last.split('/').init.mkString("/")
        }
        dir <- dirOpt
        if packages.nonEmpty // TODO check if empty package
        packageName = {
          val reservedWords = Set("trait").map("`" + _ + "`")
          val x = packages
            .flatMap(_.toString.split('.'))
            .map(p =>
              if (reservedWords(p)) { p.replace("`", "") }
              else p
            )
            .mkString("/")
          packageObjectOpt match {
            case Some(value) =>
              x + "/" + value.name.value
            case None =>
              x
          }
        }
        if packageName != dir
      } yield {
        Patch.lint(
          DirectoryPackageWarn(
            path = path,
            packageName = packageName,
            position = packages.last.pos
          )
        )
      }
    }.getOrElse(Patch.empty)
  }
}

case class DirectoryPackageWarn(path: String, packageName: String, override val position: Position) extends Diagnostic {
  override def message = s"inconsistent package and directory\n${path}\n${packageName}"

  override def severity = LintSeverity.Warning
}
