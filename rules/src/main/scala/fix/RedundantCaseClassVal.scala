package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Ctor
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.termParamClauseToValues
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class RedundantCaseClassValConfig(
  message: String
)

object RedundantCaseClassValConfig {
  val default: RedundantCaseClassValConfig = RedundantCaseClassValConfig(
    message = "redundant case class val"
  )

  implicit val surface: Surface[RedundantCaseClassValConfig] =
    metaconfig.generic.deriveSurface[RedundantCaseClassValConfig]

  implicit val decoder: ConfDecoder[RedundantCaseClassValConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class RedundantCaseClassVal(config: RedundantCaseClassValConfig) extends SyntacticRule("RedundantCaseClassVal") {

  def this() = this(RedundantCaseClassValConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("RedundantCaseClassVal")(this.config).map(newConfig => new RedundantCaseClassVal(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case c @ Defn.Class.After_4_6_0(
            _,
            _,
            _,
            Ctor.Primary.After_4_6_0(
              _,
              _,
              params1 :: _
            ),
            _
          ) if c.mods.exists(_.is[Mod.Case]) =>
        params1
          .filterNot(
            _.mods.exists(m =>
              m.is[Mod.Implicit] ||
                m.is[Mod.Private] ||
                m.is[Mod.Protected] ||
                m.is[Mod.Override] ||
                m.is[Mod.Final]
            )
          )
          .flatMap { p =>
            p.mods.find(_.is[Mod.ValParam]).map { valMod =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = config.message,
                  position = valMod.pos,
                  severity = LintSeverity.Warning
                )
              )
            }
          }
          .asPatch
    }.asPatch
  }
}
