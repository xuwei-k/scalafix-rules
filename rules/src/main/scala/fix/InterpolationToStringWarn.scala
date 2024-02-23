package fix

import scala.meta.Term
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
          case Term.Select(
                _,
                t @ Term.Name("toString")
              ) =>
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
