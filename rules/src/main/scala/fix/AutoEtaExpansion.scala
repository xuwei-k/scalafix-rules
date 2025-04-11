package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.patch.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class AutoEtaExpansion extends SyntacticRule("AutoEtaExpansion") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Term.Eta =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "remove `_` https://docs.scala-lang.org/scala3/reference/changed-features/eta-expansion.html",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    }.asPatch
  }
}
