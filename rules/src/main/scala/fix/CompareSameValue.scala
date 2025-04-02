package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionStructure
import scalafix.Patch
import scalafix.RuleName
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

private object CompareSameValue {
  private object X {
    private[this] val values = Set("==", "!=", "equals", "===", "eq", "ne")

    def unapply(t: Term): Option[(Term, Term)] = PartialFunction.condOpt(t) {
      case Term.Apply.After_4_6_0(
            Term.Select(
              a1,
              Term.Name(op)
            ),
            Term.ArgClause(a2 :: _, _)
          ) if values(op) =>
        (a1, a2)
      case Term.ApplyInfix.After_4_6_0(
            a1,
            Term.Name(op),
            _,
            Term.ArgClause(a2 :: Nil, _)
          ) if values(op) =>
        (a1, a2)
    }
  }
}

class CompareSameValue extends SyntacticRule("CompareSameValue") {
  protected def severity: LintSeverity = LintSeverity.Warning

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
            severity = severity
          )
        )
    }.asPatch
  }
}

class CompareSameValueError extends CompareSameValue {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
