package fix

import scalafix.lint.LintSeverity
import scala.meta.Term
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

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
