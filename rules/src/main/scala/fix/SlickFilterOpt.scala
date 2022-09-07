package fix

import scala.meta.Case
import scala.meta.Lit
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Term.ApplyInfix
import scala.meta.Type
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

object SlickFilterOpt {
  private object CaseSome {
    def unapply(c: Case): Option[(String, Term)] = PartialFunction.condOpt(c) {
      case Case(
            Pat.Extract(
              Term.Name("Some"),
              Pat.Var(Term.Name(a)) :: Nil
            ),
            None,
            body
          ) =>
        (a, body)
    }
  }

  private object CaseNone {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Term.Name("None") | Pat.Wildcard(),
            None,
            Term.Ascribe(
              Lit.Boolean(true),
              Type.Apply(
                Type.Name("Rep"),
                Type.Name("Boolean") :: Nil
              )
            )
          ) =>
        true
    }
  }

  private object Replace {
    def unapply(x: Term): Option[Replace] = PartialFunction.condOpt(x) {
      case Term.Match(expr, CaseSome(s, body) :: CaseNone() :: Nil) =>
        Replace(expr, s, body)
      case Term.Match(expr, CaseNone() :: CaseSome(s, body) :: Nil) =>
        Replace(expr, s, body)
    }
  }

  case class Replace private (matchExpr: Term, paramName: String, body: Term)

  private object InfixAndValues {
    def unapply(x: Term): Option[List[Term]] = {
      PartialFunction
        .condOpt(x) {
          case ApplyInfix(left, Term.Name("&&"), Nil, right :: Nil) =>
            unapply(left).toList.flatten ++ unapply(right).toList.flatten
          case _ =>
            List(x)
        }
        .filter(_.nonEmpty)
    }
  }

  object Func {
    def unapply(t: Term): Option[(String, Term, String)] = PartialFunction.condOpt(t) {
      case Term.PartialFunction(Case(p1, None, x) :: Nil) =>
        (p1.toString, x, "case ")
      case Term.Block(Term.Function(p1 :: Nil, x) :: Nil) =>
        (p1.toString, x, "")
    }
  }
}

class SlickFilterOpt extends SyntacticRule("SlickFilterOpt") {
  import SlickFilterOpt._

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply(
            Term.Select(obj, Term.Name("filter")),
            Func(p1, InfixAndValues(values), caseOpt) :: Nil
          ) if obj.collect { case CaseNone() => () }.isEmpty && values.collectFirst { case Replace(_) =>
            ()
          }.nonEmpty =>
        Patch.replaceTree(
          t,
          values.map {
            case Replace(x) =>
              s".filterOpt(${x.matchExpr}) { ${caseOpt}($p1, ${x.paramName}) => ${x.body} }"
            case x =>
              s".filter { ${caseOpt}${p1} => ${x} }"
          }.mkString(obj.toString, "", "")
        )
    }.asPatch
  }
}
