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

case class DiscardMonixTaskConfig(
  severity: LintSeverity
)

object DiscardMonixTaskConfig {
  val default: DiscardMonixTaskConfig = DiscardMonixTaskConfig(
    severity = LintSeverity.Warning,
  )

  implicit val surface: Surface[DiscardMonixTaskConfig] =
    metaconfig.generic.deriveSurface[DiscardMonixTaskConfig]

  implicit val decoder: ConfDecoder[DiscardMonixTaskConfig] = {
    import DiscardValueConfig.lintSeverityDecoderInstance
    metaconfig.generic.deriveDecoder(default)
  }
}

class DiscardMonixTask(config: DiscardMonixTaskConfig) extends SemanticRule("DiscardMonixTask") {

  def this() = this(DiscardMonixTaskConfig.default)
  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("DiscardMonixTask")(this.config).map(newConfig => new DiscardMonixTask(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val types = Seq("monix/eval/Task#")
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
