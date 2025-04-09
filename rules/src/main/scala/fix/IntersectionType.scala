package fix

import scala.meta.Type
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class IntersectionType extends SyntacticRule("IntersectionType") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Type.With =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "use `&` instead of `with`",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    }.asPatch
  }
}
