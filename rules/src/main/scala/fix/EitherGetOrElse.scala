package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Term.Block
import scala.meta.XtensionCollectionLikeUI
import scala.meta.contrib._
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object EitherGetOrElse {
  private abstract class Value(x: String) {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.Initial(Term.Name(y), Pat.Var(Term.Name(a1)) :: Nil),
            None,
            Term.Name(a2)
          ) =>
        (x == y) && (a1 == a2)
    }
  }

  private object LeftValue extends Value("Left")
  private object RightValue extends Value("Right")

  private abstract class Ignore(x: String) {
    private def asString(t: Term): String = {
      t match {
        case Block(stats) if stats.size > 1 =>
          s"{${t}}"
        case _ =>
          s"(${t})"
      }
    }

    def unapply(c: Case): Option[String] = PartialFunction.condOpt(c) {
      case Case(
            Pat.Extract.Initial(Term.Name(y), Pat.Var(Term.Name(a1)) :: Nil),
            None,
            body
          ) if (x == y) && body.collectFirst { case Term.Name(a2) if a1 == a2 => () }.isEmpty =>
        asString(body)
      case Case(
            Pat.Extract.Initial(Term.Name(y), Pat.Wildcard() :: Nil),
            None,
            body
          ) if x == y =>
        asString(body)
    }
  }

  private object RightIgnore extends Ignore("Right")
  private object LeftIgnore extends Ignore("Left")
}

class EitherGetOrElse extends SyntacticRule("EitherGetOrElse") {
  import EitherGetOrElse._
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(expr, RightValue() :: LeftIgnore(a) :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.getOrElse${a}")
      case t @ Term.Match.After_4_4_5(expr, LeftIgnore(a) :: RightValue() :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.getOrElse${a}")
      case t @ Term.Match.After_4_4_5(expr, LeftValue() :: RightIgnore(a) :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.swap.getOrElse${a}")
      case t @ Term.Match.After_4_4_5(expr, RightIgnore(a) :: LeftValue() :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.swap.getOrElse${a}")
    }.asPatch
  }
}
