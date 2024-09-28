package fix

import scala.meta.Decl
import scala.meta.Defn
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
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object SameParamOverloading {
  private case class Method(value: WithParamClauseGroup) {
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

class SameParamOverloading extends SyntacticRule("SameParamOverloading") {
  import SameParamOverloading.*
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Template =>
      val overloadMethods = t.body.stats.collect {
        case a: Defn.Def =>
          a.name -> Method(a)
        case a: Decl.Def =>
          a.name -> Method(a)
      }.groupBy(a => (a._1.value, a._2.noImplicitParams)).values.map(_.map(_._2)).filter(_.size > 1)

      overloadMethods.flatMap {
        _.map { x =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "same param overloading",
              position = x.value.pos,
              severity = LintSeverity.Warning
            )
          )
        }
      }.asPatch
    }.asPatch
  }
}
