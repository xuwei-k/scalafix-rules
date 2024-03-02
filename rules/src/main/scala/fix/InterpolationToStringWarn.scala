package fix

import scala.meta.Term
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class InterpolationToStringWarn extends SyntacticRule("InterpolationToStringWarn") {
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
              severity = LintSeverity.Warning
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
    }
  }
}
