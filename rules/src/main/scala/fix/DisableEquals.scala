package fix

import scala.meta.Position
import scala.meta.Term
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class DisableEquals extends SyntacticRule("DisableEquals") {
  override def isLinter = true
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.ApplyInfix(_, t @ Term.Name("equals"), Nil, _ :: Nil) =>
        Patch.lint(DisableEqualsWarn(t.pos))
      case Term.Apply(Term.Select(_, t @ Term.Name("equals")), _ :: Nil) =>
        Patch.lint(DisableEqualsWarn(t.pos))
    }.asPatch
  }
}

case class DisableEqualsWarn(override val position: Position) extends Diagnostic {
  override def message = "use `==` instead of `equals`"
  override def severity = LintSeverity.Error
}
