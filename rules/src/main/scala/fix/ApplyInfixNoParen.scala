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
            Term.ApplyInfix.After_4_6_0(
              Lit.Boolean(false),
              Term.Name("=="),
              Type.ArgClause(Nil),
              Term.ArgClause(List(Lit.Boolean(true)), None)
            ),
            Term.Name("&&"),
            Type.ArgClause(Nil),
            Term.ArgClause(List(Lit.Boolean(false)), None)
          ) =>
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
