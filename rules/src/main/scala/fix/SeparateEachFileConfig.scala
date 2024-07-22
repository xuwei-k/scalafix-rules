package fix

import java.util.Locale
import metaconfig.ConfDecoder
import metaconfig.generic.Surface
import scalafix.lint.LintSeverity

case class SeparateEachFileConfig(
  limit: Int,
  severity: LintSeverity
)

object SeparateEachFileConfig {

  val default: SeparateEachFileConfig = SeparateEachFileConfig(
    limit = 2,
    severity = LintSeverity.Warning,
  )

  implicit val surface: Surface[SeparateEachFileConfig] =
    metaconfig.generic.deriveSurface[SeparateEachFileConfig]

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

  implicit val decoder: ConfDecoder[SeparateEachFileConfig] =
    metaconfig.generic.deriveDecoder(default)
}
