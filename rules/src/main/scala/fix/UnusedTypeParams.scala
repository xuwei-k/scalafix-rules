package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.typeParamClauseToValues
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class UnusedTypeParamsConfig(
  message: String
)

object UnusedTypeParamsConfig {
  val default: UnusedTypeParamsConfig = UnusedTypeParamsConfig(
    message = "maybe unused type param"
  )

  implicit val surface: Surface[UnusedTypeParamsConfig] =
    metaconfig.generic.deriveSurface[UnusedTypeParamsConfig]

  implicit val decoder: ConfDecoder[UnusedTypeParamsConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class UnusedTypeParams(config: UnusedTypeParamsConfig) extends SyntacticRule("UnusedTypeParams") {

  def this() = this(UnusedTypeParamsConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("UnusedTypeParams")(this.config).map(newConfig => new UnusedTypeParams(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Def if !t.mods.exists(_.is[Mod.Override]) =>
        val typeParams =
          t.paramClauseGroups.flatMap(_.tparamClause).filter(p => p.bounds.context.isEmpty && p.bounds.view.isEmpty)
        val typeParamsMap = typeParams.map(t => t.name.value -> t).toMap
        val names = t.collect {
          case x: Term.Name =>
            typeParamsMap.get(x.value)
          case x: Type.Name =>
            typeParamsMap.get(x.value)
          case x: scala.meta.Name =>
            typeParamsMap.get(x.value)
        }.flatten
        names
          .groupBy(_.name.value)
          .values
          .collect { case a :: Nil =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = config.message,
                position = a.pos,
                severity = LintSeverity.Warning
              )
            )
          }
          .asPatch
    }.asPatch
  }
}
