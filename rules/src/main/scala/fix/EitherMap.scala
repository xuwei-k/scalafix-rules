package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class EitherMap extends SyntacticRule("EitherMap") {
  private object RightMapIdentity {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.Initial(Term.Name("Right"), a1 :: Nil),
            None,
            Term.Apply.Initial(Term.Name("Right"), a2 :: Nil)
          ) if a1.toString == a2.toString =>
        true
    }
  }

  private object LeftMapIdentity {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.Initial(Term.Name("Left"), a1 :: Nil),
            None,
            Term.Apply.Initial(Term.Name("Left"), a2 :: Nil)
          ) if a1.toString == a2.toString =>
        true
    }
  }

  private object LeftToLeft {
    def unapply(c: Case): Option[(Term.Name, Term)] = PartialFunction.condOpt(c) {
      case Case(
            Pat.Extract.Initial(Term.Name("Left"), Pat.Var(a1) :: Nil),
            None,
            Term.Apply.Initial(Term.Name("Left"), arg :: Nil)
          ) =>
        (a1, arg)
    }
  }

  private object RightToRight {
    def unapply(c: Case): Option[(Term.Name, Term)] = PartialFunction.condOpt(c) {
      case Case(
            Pat.Extract.Initial(Term.Name("Right"), Pat.Var(a1) :: Nil),
            None,
            Term.Apply.Initial(Term.Name("Right"), arg :: Nil)
          ) =>
        (a1, arg)
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(expr, RightMapIdentity() :: LeftToLeft(arg, fun) :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.left.map($arg => $fun)")
      case t @ Term.Match.After_4_4_5(expr, LeftToLeft(arg, fun) :: RightMapIdentity() :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.left.map($arg => $fun)")
      case t @ Term.Match.After_4_4_5(expr, LeftMapIdentity() :: RightToRight(arg, fun) :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.map($arg => $fun)")
      case t @ Term.Match.After_4_4_5(expr, RightToRight(arg, fun) :: LeftMapIdentity() :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.map($arg => $fun)")
    }
  }.asPatch
}
