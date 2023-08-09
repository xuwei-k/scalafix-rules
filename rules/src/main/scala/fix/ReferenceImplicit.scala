package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionTreeScalafix

class ReferenceImplicit extends SemanticRule("ReferenceImplicit") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(_, x) =>
        x.symbol.info
          .filter(_.isImplicit)
          .map { _ =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "do no use implicit value by explicit",
                position = x.pos,
                severity = LintSeverity.Warning
              )
            )
          }
          .asPatch
    }.asPatch
  }
}

