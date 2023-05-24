package fix

import scala.meta.Lit
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class MissingStripMargin extends SyntacticRule("MissingStripMargin") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Lit.String(str) =>
      val lines = str.linesIterator.toList.drop(1)
      if (
        lines.size > 1 && lines.forall(_.trim.startsWith("|")) &&
        !t.parent
          .flatMap(_.parent)
          .exists(
            _.structure.contains("stripMargin")
          )
      ) {
        Patch.lint(
          Diagnostic(
            id = "",
            message = "maybe missing stripMargin",
            position = t.parent.getOrElse(t).pos,
            severity = LintSeverity.Warning
          )
        )
      } else {
        Patch.empty
      }
    }.asPatch
  }
}
