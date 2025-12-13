package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Term.Block
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.contrib.XtensionTreeOps
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object EitherFold {

  private object RightValue {
    def unapply(value: Case): Option[Fun] = PartialFunction.condOpt(value) {
      case Case(
            Pat.Extract.Initial(Term.Name("Right"), a1 :: Nil),
            None,
            a2
          ) if a2.collectFirst { case ExtractMatch(_, _) => () }.isEmpty =>
        Fun(a1, a2)
    }
  }

  private object LeftValue {
    def unapply(value: Case): Option[Fun] = PartialFunction.condOpt(value) {
      case Case(
            Pat.Extract.Initial(Term.Name("Left"), a1 :: Nil),
            None,
            a2
          ) if a2.collectFirst { case ExtractMatch(_, _) => () }.isEmpty =>
        Fun(a1, a2)
    }
  }

  private case class Fun(private val pat: Pat, private val body: Term) {
    def asString: String = {
      val extract: Boolean = pat match {
        case _: Pat.Var | Pat.Wildcard() =>
          false
        case _ =>
          true
      }
      def block: Boolean = {
        body match {
          case Block(stats) if stats.size > 1 && !body.tokens.forall(_.is[Token.LeftBrace]) =>
            true
          case _ =>
            false
        }
      }
      if (extract) {
        s"{ case $pat => $body }"
      } else if (block) {
        s"{ $pat => $body }"
      } else {
        body match {
          case Block(Nil) =>
            s"$pat => {}"
          case _ =>
            s"$pat => $body"
        }
      }
    }
  }

  private case class Functions(left: Fun, right: Fun)

  private object ExtractFunctions {
    def unapply(cases: List[Case]): Option[Functions] = PartialFunction.condOpt(cases) {
      case LeftValue(l) :: RightValue(r) :: Nil => Functions(l, r)
      case RightValue(r) :: LeftValue(l) :: Nil => Functions(l, r)
    }
  }

  private object ExtractMatch {
    def unapply(t: Term.Match): Option[(Term, Functions)] = PartialFunction.condOpt(t) {
      case Term.Match.After_4_4_5(
            expr,
            cases @ ExtractFunctions(f),
            _
          ) if cases.forall(_.collectFirst { case _: Term.Return => () }.isEmpty) =>
        expr -> f
    }
  }
}

class EitherFold extends SyntacticRule("EitherFold") {
  import EitherFold._
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ ExtractMatch(expr, f) =>
      val e = expr match {
        case _: Term.Ascribe if !expr.tokens.headOption.exists(_.is[Token.LeftParen]) =>
          s"(${expr})"
        case _ =>
          expr.toString
      }
      Patch.replaceTree(
        t,
        s"${e}.fold(${f.left.asString}, ${f.right.asString})"
      )
    }.asPatch
  }
}
