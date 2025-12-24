package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Member
import scala.meta.Mod
import scala.meta.Template
import scala.meta.Tree.WithParamClauseGroup
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionStructure
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object SameParamOverloading {
  private case class Method(value: WithParamClauseGroup & Member.Term) {
    def noImplicitParams: Option[List[Method.ParamType]] =
      value.paramClauseGroup.map(_.paramClauses).getOrElse(Nil).find(!_.mod.exists(_.is[Mod.Implicit])).map { a =>
        a.values.map { x =>
          x.decltpe match {
            case Some(t: Type.Apply) =>
              Method.Param(t.tpe.structure)
            case Some(t: Type.ApplyInfix) =>
              Method.Param(t.op.structure)
            case Some(t: Type.Tuple) =>
              Method.Tuple(t.args.size)
            case Some(t: Type.Function) =>
              Method.Func(t.paramClause.values.size)
            case t =>
              Method.Param(t.structure)
          }
        }
      }
  }
  private object Method {
    sealed abstract class ParamType extends Product with Serializable
    case class Param(paramType: String) extends ParamType
    case class Func(size: Int) extends ParamType
    case class Tuple(size: Int) extends ParamType
  }
}

final case class SameParamOverloadingConfig(
  message: String
)

object SameParamOverloadingConfig {
  val default: SameParamOverloadingConfig = SameParamOverloadingConfig(
    message = "same param overloading"
  )

  implicit val surface: Surface[SameParamOverloadingConfig] =
    metaconfig.generic.deriveSurface[SameParamOverloadingConfig]

  implicit val decoder: ConfDecoder[SameParamOverloadingConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class SameParamOverloading(config: SameParamOverloadingConfig) extends SyntacticRule("SameParamOverloading") {

  def this() = this(SameParamOverloadingConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("SameParamOverloading")(this.config).map(newConfig => new SameParamOverloading(newConfig))
  }
  import SameParamOverloading._
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Template =>
      val overloadMethods = t.body.stats.collect {
        case a: Defn.Def if !a.mods.exists(_.is[Mod.Override]) =>
          a.name -> Method(a)
        case a: Decl.Def if !a.mods.exists(_.is[Mod.Override]) =>
          a.name -> Method(a)
      }.groupBy(a => (a._1.value, a._2.noImplicitParams)).values.map(_.map(_._2)).filter(_.size > 1)

      overloadMethods.flatMap {
        _.map { x =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = config.message,
              position = x.value.name.pos,
              severity = LintSeverity.Warning
            )
          )
        }
      }.asPatch
    }.asPatch
  }
}
