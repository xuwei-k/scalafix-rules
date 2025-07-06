package fix

import scala.meta.Importee
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class Scala3ImportWarn extends SyntacticRule("Scala3ImportWarn") {
  override def isLinter = true
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Importee.Wildcard if t.toString == "_" =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "use `*` instead of `_` for wildcard import",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
