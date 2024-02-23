package fix

import scala.meta.Importee
import scala.meta.XtensionCollectionLikeUI
import scala.meta.inputs.Position
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
        Patch.lint(Scala3ImportWarning(t.pos))
    }.asPatch
  }
}

case class Scala3ImportWarning(override val position: Position) extends Diagnostic {
  override def message: String = "use `*` instead of `_` for wildcard import"
  override def severity: LintSeverity = LintSeverity.Warning
}
