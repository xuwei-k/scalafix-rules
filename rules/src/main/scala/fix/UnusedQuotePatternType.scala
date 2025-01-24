package fix

import scala.meta.Case
import scala.meta.Type
import scala.meta.Pat
import scala.meta.Term
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UnusedQuotePatternType extends SyntacticRule("UnusedQuotePatternType") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Case(
            Pat.Macro(
              Term.QuotedMacroExpr(x1)
            ),
            x2,
            x3
          ) =>
        val usedTypes: Set[String] = Seq[Seq[String]](
          x2.collect { case t: Type.Name => t.value }.toSeq,
          x3.collect { case t: Type.Name => t.value }
        ).flatten.toSet
        x1.collect { case t: Type.Name => t }
          .groupBy(_.value)
          .toList
          .collect { case (_, Seq(t)) => t }
          .filterNot(t => usedTypes(t.value))
          .filter(_.value.head.isLower)
          .map { unused =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "",
                position = unused.pos,
                severity = LintSeverity.Error
              )
            )
          }
          .asPatch
    }.asPatch
  }
}
