package fix

import scala.meta.Importee
import scala.meta.XtensionCollectionLikeUI
import scala.meta.inputs.Position
import scalafix.Patch
import scalafix.RuleName
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class Scala3ImportWarn extends SyntacticRule("Scala3ImportWarn") {
  override def isLinter = true
  protected def severity: LintSeverity = LintSeverity.Warning
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Importee.Wildcard if t.toString == "_" =>
        Patch.lint(Scala3ImportWarning(t.pos, severity))
    }.asPatch
  }
}

case class Scala3ImportWarning(
  override val position: Position,
  override val severity: LintSeverity
) extends Diagnostic {
  override def message: String = "use `*` instead of `_` for wildcard import"
}

class Scala3ImportError extends Scala3ImportWarn {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
