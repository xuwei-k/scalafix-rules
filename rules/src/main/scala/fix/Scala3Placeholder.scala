package fix

import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scala.meta.inputs.Position
import scalafix.Patch
import scalafix.RuleName
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class Scala3Placeholder extends SyntacticRule("Scala3Placeholder") {

  protected def severity: LintSeverity = LintSeverity.Warning

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Type.Wildcard(bounds)
          if bounds.context.isEmpty && bounds.lo.isEmpty && bounds.hi.isEmpty && (t.toString == "_") =>
        Seq(
          Patch.lint(
            Scala3PlaceholderWarn(t.pos, severity)
          ),
          Patch.replaceTree(t, "?")
        ).asPatch
    }.asPatch
  }
}

case class Scala3PlaceholderWarn(
  override val position: Position,
  override val severity: LintSeverity
) extends Diagnostic {
  override def message: String = "use ? instead of _"
}

class Scala3PlaceholderError extends Scala3Placeholder {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
