package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

private object CompareSameValue {
  private object X {
    private[this] val values = Set("==", "!=", "equals", "===", "eq", "ne")

    def unapply(t: Term): Option[(Term, Term)] = PartialFunction.condOpt(t) {
      case Term.Apply(
            Term.Select(
              a1,
              Term.Name(op)
            ),
            List(a2)
          ) if values(op) =>
        (a1, a2)
      case Term.ApplyInfix(
            a1,
            Term.Name(op),
            _,
            List(a2)
          ) if values(op) =>
        (a1, a2)
    }
  }
}

class CompareSameValue extends SyntacticRule("CompareSameValue") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ CompareSameValue.X(a1, a2) if a1.structure == a2.structure && t.collect {
            case t if t.is[Term.Placeholder] => ()
          }.isEmpty =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "compare same values!?",
            position = t.pos,
            explanation = "",
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}