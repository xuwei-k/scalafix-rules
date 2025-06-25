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

final case class AutoEtaExpansionConfig(
  message: String
)

object AutoEtaExpansionConfig {
  val default: AutoEtaExpansionConfig = AutoEtaExpansionConfig(
    message = "remove `_` https://docs.scala-lang.org/scala3/reference/changed-features/eta-expansion.html"
  )

  implicit val surface: Surface[AutoEtaExpansionConfig] =
    metaconfig.generic.deriveSurface[AutoEtaExpansionConfig]

  implicit val decoder: ConfDecoder[AutoEtaExpansionConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class AutoEtaExpansion(config: AutoEtaExpansionConfig) extends SyntacticRule("AutoEtaExpansion") {

  def this() = this(AutoEtaExpansionConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("AutoEtaExpansion")(this.config).map(newConfig => new AutoEtaExpansion(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Term.Eta =>
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
