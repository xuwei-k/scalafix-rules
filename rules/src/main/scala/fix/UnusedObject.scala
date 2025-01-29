package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.transversers.*
import scala.meta.XtensionClassifiable
import scala.meta.contrib.XtensionTreeOps
import scala.meta.inputs.Input
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionOptionPatch

object UnusedObject {
  private case class Problem(file: String, value: String)

  private lazy val problems: Seq[Problem] = {
    // https://xuwei-k.hatenablog.com/entry/2025/01/28/104501
    (scala.xml.XML.loadFile("IntelliJ IDEAで吐き出したScalaUnusedSymbol.xmlのファイル")
      \\ "problem").map { elem =>
      Problem(
        file = (elem \ "file").text.drop("file://$PROJECT_DIR$/".length),
        value = (elem \ "description").text
      )
    }.flatMap(p =>
      p.value match {
        case s"Object '${objectName}' is never used" =>
          Some(p.copy(value = objectName))
        case _ =>
          None
      }
    )
  }
}

class UnusedObject extends SyntacticRule("UnusedObject") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    PartialFunction
      .condOpt(doc.input) { case f: Input.VirtualFile =>
        doc.tree.collect {
          case t: Defn.Object
              if UnusedObject.problems
                .exists(p => (p.value == t.name.value) && f.path.contains(p.file)) && t.collectFirst {
                case x: Defn.Def if x.mods.exists(_.is[Mod.Implicit]) =>
                  ()
                case x: Defn.Val if x.mods.exists(_.is[Mod.Implicit]) =>
                  ()
              }.isEmpty =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "このobject使ってない？",
                position = t.pos,
                severity = LintSeverity.Error
              )
            )
        }.asPatch
      }
      .asPatch
  }
}
