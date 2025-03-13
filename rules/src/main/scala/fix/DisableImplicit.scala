package fix

import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch
import scala.meta.tokens.Token

class DisableImplicit extends SyntacticRule("DisableImplicit") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tokens.collect { case t: Token.KwImplicit =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "don't use implicit",
          position = t.pos,
          severity = LintSeverity.Error
        )
      )
    }.asPatch
  }
}
