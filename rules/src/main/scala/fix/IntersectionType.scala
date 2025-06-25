package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Type
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class IntersectionTypeConfig(
  message: String
)

object IntersectionTypeConfig {
  val default: IntersectionTypeConfig = IntersectionTypeConfig(
    message = "use `&` instead of `with`"
  )

  implicit val surface: Surface[IntersectionTypeConfig] =
    metaconfig.generic.deriveSurface[IntersectionTypeConfig]

  implicit val decoder: ConfDecoder[IntersectionTypeConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class IntersectionType(config: IntersectionTypeConfig) extends SyntacticRule("IntersectionType") {

  def this() = this(IntersectionTypeConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("IntersectionType")(this.config).map(newConfig => new IntersectionType(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Type.With =>
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
