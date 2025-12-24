package fix

import scala.meta.Case
import scala.meta.Lit
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Term.ApplyInfix
import scala.meta.Term.Block
import scala.meta.Tree
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object SlickFilter {
  private object CaseSome {
    def unapply(c: Case): Option[(String, Term)] = PartialFunction.condOpt(c) {
      case Case(
            Pat.Extract.After_4_6_0(
              Term.Name("Some"),
              Pat.ArgClause(Pat.Var(Term.Name(a)) :: Nil)
            ),
            None,
            body
          ) =>
        (a, body)
    }
  }

  private object TrueRepBoolean {
    def unapply(x: Term): Boolean = PartialFunction.cond(x) {
      case Term.Ascribe(
            Lit.Boolean(true),
            Type.Apply.Initial(
              Type.Name("Rep"),
              Type.Name("Boolean") :: Nil
            )
          ) =>
        true
    }
  }

  private object CaseNone {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Term.Name("None") | Pat.Wildcard(),
            None,
            TrueRepBoolean()
          ) =>
        true
    }
  }

  private object ReplaceFilterOpt {
    def unapply(x: Tree): Option[ReplaceFilterOpt] = PartialFunction.condOpt(x) {
      case Term.Match.After_4_4_5(expr, CaseSome(s, body) :: CaseNone() :: Nil, _) =>
        ReplaceFilterOpt(expr, s, body)
      case Term.Match.After_4_4_5(expr, CaseNone() :: CaseSome(s, body) :: Nil, _) =>
        ReplaceFilterOpt(expr, s, body)
    }
  }

  case class ReplaceFilterOpt private (matchExpr: Term, paramName: String, body: Term)

  private object ReplaceFilterIf {
    def unapply(x: Tree): Option[ReplaceFilterIf] = PartialFunction.condOpt(x) {
      case Term.If.After_4_4_0(cond, TrueRepBoolean(), expr, _) =>
        ReplaceFilterIf(cond, expr, true)
      case Term.If.After_4_4_0(cond, Block(TrueRepBoolean() :: Nil), expr, _) =>
        ReplaceFilterIf(cond, expr, true)
      case Term.If.After_4_4_0(cond, expr, TrueRepBoolean(), _) =>
        ReplaceFilterIf(cond, expr, false)
      case Term.If.After_4_4_0(cond, expr, Block(TrueRepBoolean() :: Nil), _) =>
        ReplaceFilterIf(cond, expr, false)
    }
  }

  case class ReplaceFilterIf private (cond: Term, expr: Term, thenIsTrue: Boolean)

  private object InfixAndValues {
    def unapply(x: Term): Option[List[Term]] = {
      PartialFunction.condOpt(x) {
        case ApplyInfix.After_4_6_0(left, Term.Name("&&"), Type.ArgClause(Nil), Term.ArgClause(right :: Nil, None)) =>
          unapply(left).toList.flatten ++ unapply(right).toList.flatten
        case _ =>
          x :: Nil
      }
    }
  }

  object Func {
    def unapply(t: Term): Option[(String, Term, String, Boolean)] = PartialFunction.condOpt(t) {
      case Term.PartialFunction(Case(p1, None, x) :: Nil) =>
        (p1.toString, x, "case ", true)
      case Term.Block(
            Term.Function.After_4_6_0(
              Term.ParamClause(
                p1 :: Nil,
                None
              ),
              x
            ) :: Nil
          ) =>
        (p1.toString, x, "", true)
      case Term.Function.After_4_6_0(
            Term.ParamClause(
              p1 :: Nil,
              None
            ),
            x
          ) =>
        (p1.toString, x, "", false)
    }
  }
}

class SlickFilter extends SyntacticRule("SlickFilter") {
  import SlickFilter._

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            Term.Select(obj, Term.Name("filter")),
            Term.ArgClause(
              Func(p1, InfixAndValues(values), caseOpt, block) :: Nil,
              None
            )
          ) if obj.collect {
            case ReplaceFilterIf(_) =>
              ()
            case ReplaceFilterOpt(_) =>
              ()
          }.isEmpty && values.collectFirst {
            case ReplaceFilterOpt(_) =>
              ()
            case ReplaceFilterIf(_) =>
              ()
          }.nonEmpty =>
        val (open, close) = {
          if (block) {
            ("{", "}")
          } else {
            ("(", ")")
          }
        }

        Patch.replaceTree(
          t,
          values.map {
            case ReplaceFilterOpt(x) =>
              s".filterOpt(${x.matchExpr}) { ${caseOpt}($p1, ${x.paramName}) => ${x.body} }"
            case ReplaceFilterIf(x) =>
              val unary = if (x.thenIsTrue) "!" else ""
              val cond0 = if (x.cond.toString.contains(" ") && x.thenIsTrue) s"(${x.cond})" else x.cond.toString
              val param = if (caseOpt.nonEmpty) s"($p1)" else p1
              s".filterIf(${unary}${cond0}) ${open} ${caseOpt}${param} => ${x.expr} ${close}"
            case x =>
              s".filter${open} ${caseOpt}${p1} => ${x} ${close}"
          }.mkString(obj.toString, "", "")
        )
    }.asPatch
  }
}
