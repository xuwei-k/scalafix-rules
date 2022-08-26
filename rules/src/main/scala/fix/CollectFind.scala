package fix

import scalafix.Diagnostic
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term
import scala.meta.inputs.Position
import scalafix.lint.LintSeverity

class CollectFind extends SyntacticRule("CollectFind") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply(
            Term.Select(
              Term.Apply(
                Term.Select(
                  _,
                  Term.Name("collect")
                ),
                _ :: Nil
              ),
              Term.Name("find")
            ),
            _ :: Nil
          ) =>
        Patch.lint(CollectFindWarn(t.pos))
    }
  }.asPatch
}

case class CollectFindWarn(override val position: Position) extends Diagnostic {
  override def message = "maybe you can use collectFirst"
  override def severity = LintSeverity.Warning
}
