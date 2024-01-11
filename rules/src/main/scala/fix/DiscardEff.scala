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

case class DiscardEffConfig(
  severity: LintSeverity
)

object DiscardEffConfig {
  val default: DiscardEffConfig = DiscardEffConfig(
    severity = LintSeverity.Warning,
  )

  implicit val surface: Surface[DiscardEffConfig] =
    metaconfig.generic.deriveSurface[DiscardEffConfig]

  implicit val decoder: ConfDecoder[DiscardEffConfig] = {
    import DiscardValueConfig.lintSeverityDecoderInstance
    metaconfig.generic.deriveDecoder(default)
  }
}

class DiscardEff(config: DiscardEffConfig) extends SemanticRule("DiscardEff") {

  def this() = this(DiscardEffConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("DiscardEff")(this.config).map(newConfig => new DiscardEff(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val types = Seq("org/atnos/eff/Eff#")
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
