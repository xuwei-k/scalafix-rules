package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.patch.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class CaseClassExplicitCopyConfig(
  message: String
)

object CaseClassExplicitCopyConfig {
  val default: CaseClassExplicitCopyConfig = CaseClassExplicitCopyConfig(
    message = "Don't define `copy` method in case class"
  )

  implicit val surface: Surface[CaseClassExplicitCopyConfig] =
    metaconfig.generic.deriveSurface[CaseClassExplicitCopyConfig]

  implicit val decoder: ConfDecoder[CaseClassExplicitCopyConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class CaseClassExplicitCopy(config: CaseClassExplicitCopyConfig) extends SyntacticRule("CaseClassExplicitCopy") {

  def this() = this(CaseClassExplicitCopyConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("CaseClassExplicitCopy")(this.config).map(newConfig => new CaseClassExplicitCopy(newConfig))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class if t.mods.exists(_.is[Mod.Case]) && !t.mods.exists(_.is[Mod.Abstract]) =>
        t.templ.body.stats.collect {
          case x: Defn.Def if x.name.value == "copy" =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = config.message,
                position = x.pos,
                severity = LintSeverity.Warning
              )
            )
        }.asPatch
    }.asPatch
  }
}
