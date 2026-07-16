package fix

import scala.meta._
import scala.meta.tokens.Token
import scalafix.lint.LintSeverity
import scalafix.v1._

class DubiousLiteral extends SyntacticRule("DubiousLiteral") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Lit.Int(v),
            _
          ) if (v < 0) && t.tokens.forall(!_.is[Token.RightParen]) =>
        t
      case t: Lit.Int if t.tokens.exists(_.is[Token.Space]) =>
        t
    }.map { t =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "dubious literal https://github.com/scala/scala3/pull/24163",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    }.asPatch
  }
}
