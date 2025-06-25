package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Patch
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

final case class ParameterUntuplingCaseWarnConfig(
  message: String
)

object ParameterUntuplingCaseWarnConfig {
  val default: ParameterUntuplingCaseWarnConfig = ParameterUntuplingCaseWarnConfig(
    message =
      "unnecessary `case` if scala 3 https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling.html"
  )

  implicit val surface: Surface[ParameterUntuplingCaseWarnConfig] =
    metaconfig.generic.deriveSurface[ParameterUntuplingCaseWarnConfig]

  implicit val decoder: ConfDecoder[ParameterUntuplingCaseWarnConfig] =
    metaconfig.generic.deriveDecoder(default)
}

/**
 * [[https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling.html]]
 * [[https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling-spec.html]]
 */
class ParameterUntuplingCaseWarn(config: ParameterUntuplingCaseWarnConfig)
    extends SyntacticRule("ParameterUntuplingCaseWarn") {

  def this() = this(ParameterUntuplingCaseWarnConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("ParameterUntuplingCaseWarn")(this.config)
      .map(newConfig => new ParameterUntuplingCaseWarn(newConfig))
  }
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
                message = config.message,
                position = c.pos,
                severity = LintSeverity.Warning
              )
            )
          )
          .asPatch
    }.asPatch
  }
}
