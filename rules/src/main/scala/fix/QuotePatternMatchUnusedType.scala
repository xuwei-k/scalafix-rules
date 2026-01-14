package fix

import scala.meta.Case
import scala.meta.Name
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class QuotePatternMatchUnusedType extends SyntacticRule("QuotePatternMatchUnusedType") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Case(
            Pat.Macro(
              Term.QuotedMacroExpr(
                pattern
              )
            ),
            _,
            _
          ) =>
        val names: Set[String] = Seq(
          t.cond.map(_.collect { case n: Name => n.value }).toSeq.flatten,
          t.body.collect { case n: Name => n.value },
        ).flatten.toSet
        pattern.collect { case tpe: Type.Name =>
          tpe
        }.groupBy(_.value)
          .collect {
            case (_, Seq(tpe)) if tpe.value.headOption.exists(c => 'a' <= c && c <= 'z') && !names(tpe.value) =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = "unused",
                  position = tpe.pos,
                  severity = LintSeverity.Warning
                )
              )
          }
          .asPatch
    }.asPatch
  }
}
