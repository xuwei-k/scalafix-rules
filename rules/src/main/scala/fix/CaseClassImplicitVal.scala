package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class CaseClassImplicitValConfig(
  message: String
)

object CaseClassImplicitValConfig {
  val default: CaseClassImplicitValConfig = CaseClassImplicitValConfig(
    message = "`case class must have at least one leading non-implicit parameter list` error in Scala 3"
  )

  implicit val surface: Surface[CaseClassImplicitValConfig] =
    metaconfig.generic.deriveSurface[CaseClassImplicitValConfig]

  implicit val decoder: ConfDecoder[CaseClassImplicitValConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class CaseClassImplicitVal(config: CaseClassImplicitValConfig) extends SyntacticRule("CaseClassImplicitVal") {

  def this() = this(CaseClassImplicitValConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("CaseClassImplicitVal")(this.config).map(newConfig => new CaseClassImplicitVal(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class
          if t.mods.exists(_.is[Mod.Case]) && t.ctor.paramClauses.headOption.exists(
            _.values.exists(_.mods.exists(_.is[Mod.Implicit]))
          ) =>
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
