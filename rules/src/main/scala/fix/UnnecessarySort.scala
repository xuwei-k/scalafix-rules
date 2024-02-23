package fix

import scala.meta.Position
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object UnnecessarySort {
  val scala213Methods: Map[String, String] = Map(
    "headOption" -> "minByOption",
    "lastOption" -> "maxByOption"
  )
  val map: Map[String, String] = Map(
    "head" -> "minBy",
    "last" -> "maxBy",
  ) ++ scala213Methods
}

class UnnecessarySort extends SyntacticRule("UnnecessarySort") {
  override def isLinter: Boolean = true

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(_, Term.Name("sortBy")),
              Term.ArgClause(_ :: Nil, _)
            ),
            Term.Name(methodName)
          ) if UnnecessarySort.map.contains(methodName) =>
        Patch.lint(
          UnnecessarySortWarn(t.pos, s"maybe you can use ${UnnecessarySort.map(methodName)}")
        )
    }.asPatch
  }
}

case class UnnecessarySortWarn(override val position: Position, message: String) extends Diagnostic {
  override def severity: LintSeverity = LintSeverity.Warning
}
