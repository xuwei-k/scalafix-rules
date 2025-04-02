package fix

import scala.meta.Term
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.rule.RuleName
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class InterpolationToStringWarn extends SyntacticRule("InterpolationToStringWarn") {
  protected def severity: LintSeverity = LintSeverity.Warning

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Interpolate(
            Term.Name("s"),
            _,
            values
          ) =>
        values.collect {
          case InterpolationToStringWarn.SelectToString(t) =>
            t
          case Term.Block(InterpolationToStringWarn.SelectToString(t) :: Nil) =>
            t
        }.map { t =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "maybe unnecessary `toString` in the `s` interpolation",
              position = t.pos,
              severity = severity
            )
          )
        }.asPatch
    }
  }.asPatch
}

private object InterpolationToStringWarn {
  private object SelectToString {
    def unapply(t: Tree): Option[Term.Name] = PartialFunction.condOpt(t) {
      case Term.Select(
            _,
            x @ Term.Name("toString")
          ) =>
        x
      case Term.Apply.After_4_6_0(
            Term.Select(_, x @ Term.Name("toString")),
            Term.ArgClause(Nil, None)
          ) =>
        x
    }
  }
}

class InterpolationToStringError extends InterpolationToStringWarn {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
