package fix

import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Member
import scala.meta.Mod
import scala.meta.Stat
import scala.meta.Template
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
  private sealed abstract class Method extends Product with Serializable {
    def value: Stat
    def paramClauseGroups: List[Member.ParamClauseGroup]
    def noImplicitParams: Option[List[Method.Param]] =
      paramClauseGroups.headOption.map(_.paramClauses).filter(!_.forall(_.mod.exists(_.is[Mod.Implicit]))).map { a =>
        a.flatMap(_.values).map { x =>
          Method.Param(
            paramType = x.decltpe.map(_.structure)
          )
        }
      }
  }
  private object Method {
    case class Param(paramType: Option[String])

    final case class Defn(value: scala.meta.Defn.Def) extends Method {
      def paramClauseGroups = value.paramClauseGroups
    }
    final case class Decl(value: scala.meta.Decl.Def) extends Method {
      def paramClauseGroups = value.paramClauseGroups
    }
  }
}

class SameParamOverloading extends SyntacticRule("SameParamOverloading") {
  import SameParamOverloading.*
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Template =>
      val overloadMethods = t.stats.collect {
        case a: Defn.Def =>
          a.name -> Method.Defn(a)
        case a: Decl.Def =>
          a.name -> Method.Decl(a)
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
