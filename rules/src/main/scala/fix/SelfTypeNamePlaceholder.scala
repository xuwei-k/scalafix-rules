package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Name
import scala.meta.Self
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class SelfTypeNamePlaceholderConfig(
  message: String
)

object SelfTypeNamePlaceholderConfig {
  val default: SelfTypeNamePlaceholderConfig = SelfTypeNamePlaceholderConfig(
    message = "Don't use `_` for Scala 3"
  )

  implicit val surface: Surface[SelfTypeNamePlaceholderConfig] =
    metaconfig.generic.deriveSurface[SelfTypeNamePlaceholderConfig]

  implicit val decoder: ConfDecoder[SelfTypeNamePlaceholderConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class SelfTypeNamePlaceholder(config: SelfTypeNamePlaceholderConfig) extends SyntacticRule("SelfTypeNamePlaceholder") {

  def this() = this(SelfTypeNamePlaceholderConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("SelfTypeNamePlaceholder")(this.config)
      .map(newConfig => new SelfTypeNamePlaceholder(newConfig))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Self(
            t @ Name.Placeholder(),
            _
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = config.message,
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
