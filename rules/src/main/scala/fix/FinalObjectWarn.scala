package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

final case class FinalObjectWarnConfig(
  message: String
)

object FinalObjectWarnConfig {
  val default: FinalObjectWarnConfig = FinalObjectWarnConfig(
    message = "Modifier final is redundant"
  )

  implicit val surface: Surface[FinalObjectWarnConfig] =
    metaconfig.generic.deriveSurface[FinalObjectWarnConfig]

  implicit val decoder: ConfDecoder[FinalObjectWarnConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class FinalObjectWarn(config: FinalObjectWarnConfig) extends SyntacticRule("FinalObjectWarn") {

  def this() = this(FinalObjectWarnConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("FinalObjectWarn")(this.config).map(newConfig => new FinalObjectWarn(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Defn.Object =>
      t.mods
        .find(_.is[Mod.Final])
        .map { finalMod =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = config.message,
              position = finalMod.pos,
              severity = LintSeverity.Warning
            )
          )
        }
        .asPatch
    }.asPatch
  }
}
