package fix

import scala.meta._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ApplyInfixNoParen extends SyntacticRule("ApplyInfixNoParen") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.ApplyInfix.After_4_6_0(
            _: Term.ApplyInfix,
            _,
            _,
            _
          ) if t.tokens.forall(!_.is[Token.LeftParen]) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
