package fix

import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term
import scala.meta.Position

object UnnecessarySort {
  val map: Map[String, String] = Map(
    "head" -> "minBy",
    "headOption" -> "minByOption",
    "last" -> "maxBy",
    "lastOption" -> "maxByOption"
  )
}

class UnnecessarySort extends SyntacticRule("UnnecessarySort") {
  override def isLinter: Boolean = true

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply(Term.Select(_, Term.Name("sortBy")), List(_)),
            Term.Name(methodName)
          ) if UnnecessarySort.map.contains(methodName) =>
        Patch.lint(
          UnnecessarySortWarn(t.pos, s"maybe you can use ${UnnecessarySort.map(methodName)}")
        )
    }.asPatch
  }
}

case class UnnecessarySortWarn(override val position: Position, message: String) extends Diagnostic {
  override def severity = LintSeverity.Warning
}
