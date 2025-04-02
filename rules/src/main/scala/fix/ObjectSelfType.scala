package fix

import scala.meta.Defn
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.RuleName
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

class ObjectSelfType extends SyntacticRule("ObjectSelfType") {
  override def isLinter = true

  protected def severity: LintSeverity = LintSeverity.Warning

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case obj: Defn.Object =>
      obj.templ.body.selfOpt.collect {
        case self if self.decltpe.isDefined =>
          Patch.lint(
            Diagnostic(
              id = "",
              position = self.pos,
              message = "objects must not have a self type",
              severity = severity,
            )
          )
      }.asPatch
    }.asPatch
  }
}

class ObjectSelfTypeError extends ObjectSelfType {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
