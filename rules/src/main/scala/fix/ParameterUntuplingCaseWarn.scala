package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.RuleName
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

/**
 * [[https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling.html]]
 * [[https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling-spec.html]]
 */
class ParameterUntuplingCaseWarn extends SyntacticRule("ParameterUntuplingCaseWarn") {
  protected def severity: LintSeverity = LintSeverity.Warning

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.PartialFunction(
            List(
              c @ Case(
                p @ Pat.Tuple(
                  values
                ),
                None,
                _
              )
            )
          ) if values.forall {
            case Pat.Var(_: Term.Name) | Pat.Wildcard() => true
            case _ => false
          } =>
        c.tokens
          .find(_.is[Token.KwCase])
          .filter(_.pos.start < p.pos.start)
          .map(c =>
            Patch.lint(
              Diagnostic(
                id = "",
                message =
                  "unnecessary `case` if scala 3 https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling.html",
                position = c.pos,
                severity = severity
              )
            )
          )
          .asPatch
    }.asPatch
  }
}

class ParameterUntuplingCaseError extends ParameterUntuplingCaseWarn {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
