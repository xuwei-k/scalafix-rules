package fix

import metaconfig.ConfDecoder
import metaconfig.generic.Surface
import scalafix.lint.LintSeverity
import java.util.Locale

case class DiscardSingleConfig(
  severity: LintSeverity
) {
  def toDiscardValueConfig(tpe: String): DiscardValueConfig = {
    val types = Seq(tpe)
    severity match {
      case LintSeverity.Info =>
        DiscardValueConfig.default.copy(info = types)
      case LintSeverity.Warning =>
        DiscardValueConfig.default.copy(warning = types)
      case LintSeverity.Error =>
        DiscardValueConfig.default.copy(error = types)
    }
  }
}

object DiscardSingleConfig {
  val default: DiscardSingleConfig = DiscardSingleConfig(
    severity = LintSeverity.Warning,
  )

  implicit val surface: Surface[DiscardSingleConfig] =
    metaconfig.generic.deriveSurface[DiscardSingleConfig]

  private implicit val lintSeverityDecoderInstance: ConfDecoder[LintSeverity] = { conf =>
    conf.as[String].map(_.toUpperCase(Locale.ROOT)).map {
      case "ERROR" =>
        LintSeverity.Error
      case "INFO" =>
        LintSeverity.Info
      case _ =>
        LintSeverity.Warning
    }
  }

  implicit val decoder: ConfDecoder[DiscardSingleConfig] =
    metaconfig.generic.deriveDecoder(default)

}
