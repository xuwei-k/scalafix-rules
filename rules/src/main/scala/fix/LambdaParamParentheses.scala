package fix

import scala.meta.Position
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.rule.RuleName
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

/**
 * unnecessary since Scala 2.13.11
 * [[https://github.com/scala/scala/pull/10320]]
 */
class LambdaParamParentheses extends SyntacticRule("LambdaParamParentheses") {
  override def isLinter = true

  protected def severity: LintSeverity = LintSeverity.Warning

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t1 @ Term.Function.Initial(param :: Nil, _) if param.decltpe.nonEmpty && param.mods.isEmpty =>
        if (t1.tokens.find(_.is[Token.LeftParen]).exists(_.pos.start <= param.pos.start)) {
          Patch.empty
        } else {
          Patch.lint(
            LambdaParamParenthesesWarn(param.pos, severity)
          )
        }
    }.asPatch
  }
}

case class LambdaParamParenthesesWarn(
  override val position: Position,
  override val severity: LintSeverity
) extends Diagnostic {
  override def message = "add parentheses or remove explicit types for prepare Scala 3"
}

class LambdaParamParenthesesError extends LambdaParamParentheses {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
