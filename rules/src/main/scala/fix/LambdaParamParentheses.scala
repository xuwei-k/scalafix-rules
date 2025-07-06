package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class LambdaParamParenthesesConfig(
  message: String
)

object LambdaParamParenthesesConfig {
  val default: LambdaParamParenthesesConfig = LambdaParamParenthesesConfig(
    message = "add parentheses or remove explicit types for prepare Scala 3"
  )

  implicit val surface: Surface[LambdaParamParenthesesConfig] =
    metaconfig.generic.deriveSurface[LambdaParamParenthesesConfig]

  implicit val decoder: ConfDecoder[LambdaParamParenthesesConfig] =
    metaconfig.generic.deriveDecoder(default)
}

/**
 * unnecessary since Scala 2.13.11
 * [[https://github.com/scala/scala/pull/10320]]
 */
class LambdaParamParentheses(config: LambdaParamParenthesesConfig) extends SyntacticRule("LambdaParamParentheses") {

  def this() = this(LambdaParamParenthesesConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("LambdaParamParentheses")(this.config).map(newConfig => new LambdaParamParentheses(newConfig))
  }
  override def isLinter = true

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t1 @ Term.Function.Initial(param :: Nil, _) if param.decltpe.nonEmpty && param.mods.isEmpty =>
        if (t1.tokens.find(_.is[Token.LeftParen]).exists(_.pos.start <= param.pos.start)) {
          Patch.empty
        } else {
          Patch.lint(
            Diagnostic(
              id = "",
              message = config.message,
              position = param.pos,
              severity = LintSeverity.Warning
            )
          )
        }
    }.asPatch
  }
}
