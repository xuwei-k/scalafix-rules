package fix

import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.lint.LintSeverity

case class DiscardScalaFutureConfig(
  severity: LintSeverity
)

object DiscardScalaFutureConfig {
  val default: DiscardScalaFutureConfig = DiscardScalaFutureConfig(
    severity = LintSeverity.Warning,
  )

  implicit val surface: Surface[DiscardScalaFutureConfig] =
    metaconfig.generic.deriveSurface[DiscardScalaFutureConfig]

  implicit val decoder: ConfDecoder[DiscardScalaFutureConfig] = {
    import DiscardValueConfig.lintSeverityDecoderInstance
    metaconfig.generic.deriveDecoder(default)
  }
}

class DiscardScalaFuture(config: DiscardScalaFutureConfig) extends SemanticRule("DiscardScalaFuture") {

  def this() = this(DiscardScalaFutureConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("DiscardScalaFuture")(this.config).map(newConfig => new DiscardScalaFuture(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val types = Seq("scala/concurrent/Future#")
    DiscardValue.typeRef(
      config.severity match {
        case LintSeverity.Info =>
          DiscardValueConfig.default.copy(info = types)
        case LintSeverity.Warning =>
          DiscardValueConfig.default.copy(warning = types)
        case LintSeverity.Error =>
          DiscardValueConfig.default.copy(error = types)
      }
    )
  }
}
