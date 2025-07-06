package fix

import scala.meta.Lit
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class NoElse extends SyntacticRule("NoElse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.If.After_4_4_0(_, _, Lit.Unit(), _) =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "add `else`",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    }.asPatch
  }
}
