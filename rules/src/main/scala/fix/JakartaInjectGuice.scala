package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.patch.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

case class JakartaInjectGuiceConfig(
  message: String
)

object JakartaInjectGuiceConfig {
  val default: JakartaInjectGuiceConfig = JakartaInjectGuiceConfig(
    message = "use com.google.inject"
  )

  implicit val surface: Surface[JakartaInjectGuiceConfig] =
    metaconfig.generic.deriveSurface[JakartaInjectGuiceConfig]

  implicit val decoder: ConfDecoder[JakartaInjectGuiceConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class JakartaInjectGuice(config: JakartaInjectGuiceConfig) extends SyntacticRule("JakartaInjectGuice") {
  def this() = this(JakartaInjectGuiceConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("JakartaInjectGuice")(this.config).map(newConfig => new JakartaInjectGuice(newConfig))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.Select(Term.Name("jakarta"), Term.Name("inject")) =>
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
