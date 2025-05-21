package fix

import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class Implicit extends SyntacticRule("Implicit") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tokens.collect { case t: Token.KwImplicit =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "don't use `implicit`. use `given`, `using` or `extension`",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    }.asPatch
  }
}
