package fix

import fix.FileNameConsistent._
import scala.meta.Defn
import scala.meta.Pkg
import scala.meta.Tree
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.inputs.Input
import scala.meta.inputs.Position
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.RuleName
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class FileNameConsistent extends SyntacticRule("FileNameConsistent") {
  override def isLinter = true

  protected def severity: LintSeverity = LintSeverity.Warning

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val defs = doc.tree.collect {
      case x: Defn.Trait if x.isTopLevel =>
        TemplateDef(x, x.name.value)
      case x: Defn.Class if x.isTopLevel =>
        TemplateDef(x, x.name.value)
      case x: Defn.Object if x.isTopLevel =>
        TemplateDef(x, x.name.value)
      case x: Defn.Enum if x.isTopLevel =>
        TemplateDef(x, x.name.value)
    }
    val packageObjects = doc.tree.collect { case x: Pkg.Object =>
      TemplateDef(x, x.name.value)
    }
    val scalaSourceOpt = PartialFunction.condOpt(doc.input) {
      case f: Input.File =>
        ScalaSource(
          fullPath = f.path.toString,
          name = f.path.toFile.getName.replace(".scala", "")
        )
      case f: Input.VirtualFile =>
        ScalaSource(
          fullPath = f.path,
          name = f.path.split(java.io.File.separatorChar).lastOption.getOrElse("").replace(".scala", "")
        )
    }

    val names = defs.map(_.name).distinct
    scalaSourceOpt match {
      case Some(src) =>
        if ((src.name == "package") && (packageObjects.size == 1) && names.isEmpty) {
          // correct
          // - file name is "package.scala"
          // - and only package object
          Patch.empty
        } else if ((names.size == 1) || (packageObjects.size == 1)) {
          if (names.contains(src.name)) {
            // correct
            Patch.empty
          } else {
            Patch.lint(
              FileNameConsistentWaring(
                names = names,
                path = src.fullPath,
                position = defs.headOption.getOrElse(packageObjects.head).tree.pos,
                severity = severity
              )
            )
          }
        } else {
          // if there are some toplevel trait, class, object
          // TODO
          Patch.empty
        }
      case _ =>
        // another input type
        Patch.empty
    }
  }
}

object FileNameConsistent {
  private case class ScalaSource(fullPath: String, name: String)

  private case class TemplateDef(tree: Tree, name: String)

  private case class FileNameConsistentWaring(
    names: List[String],
    path: String,
    override val position: Position,
    override val severity: LintSeverity
  ) extends Diagnostic {
    override def message: String = s"inconsistent file name and class name. names = ${names.mkString("[", ", ", "]")}"
  }

  implicit class TreeOps(private val self: Defn) extends AnyVal {
    def isTopLevel: Boolean =
      self.parent.exists(_.is[Pkg.Body]) || self.parent.isEmpty
  }
}

class FileNameConsistentError extends FileNameConsistent {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
