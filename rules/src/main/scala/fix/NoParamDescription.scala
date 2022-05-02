package fix

import scalafix.lint.LintSeverity
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.contrib._
import scala.meta.inputs.Position

class NoParamDescription extends SyntacticRule("NoParamDescription") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t =>
        doc.comments
          .leading(t)
          .filter(
            _.docTokens.toList.flatten.exists(_.name.exists(_ contains "\n@param"))
          ).map { c =>
            val i = c.value.indexOf("@param")
            val start = c.start + i
            val end = start + c.value.drop(i).indexOf('\n') + 2
            val pos = Position.Range(
              input = c.pos.input,
              start = start,
              end = end
            )
            Patch.lint(NoParamDescriptionWran(pos))
          }.asPatch
    }.asPatch
  }
}

case class NoParamDescriptionWran(override val position: Position) extends Diagnostic {
  override def message = "no @param description"
  override def severity = LintSeverity.Warning
}
