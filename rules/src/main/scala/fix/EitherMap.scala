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
            Pat.Extract.After_4_6_0(
              Term.Name("Right"),
              Pat.ArgClause(a1 :: Nil)
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Right"),
              Term.ArgClause(
                a2 :: Nil,
                None
              )
            )
          ) if a1.toString == a2.toString =>
        true
    }
  }

  private object LeftMapIdentity {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.After_4_6_0(
              Term.Name("Left"),
              Pat.ArgClause(a1 :: Nil)
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Left"),
              Term.ArgClause(
                a2 :: Nil,
                None
              )
            )
          ) if a1.toString == a2.toString =>
        true
    }
  }

  private object LeftToLeft {
    def unapply(c: Case): Option[(Term.Name, Term)] = PartialFunction.condOpt(c) {
      case Case(
            Pat.Extract.After_4_6_0(Term.Name("Left"), Pat.ArgClause(Pat.Var(a1) :: Nil)),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Left"),
              Term.ArgClause(
                arg :: Nil,
                None
              )
            )
          ) =>
        (a1, arg)
    }
  }

  private object RightToRight {
    def unapply(c: Case): Option[(Term.Name, Term)] = PartialFunction.condOpt(c) {
      case Case(
            Pat.Extract.After_4_6_0(Term.Name("Right"), Pat.ArgClause(Pat.Var(a1) :: Nil)),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Right"),
              Term.ArgClause(
                arg :: Nil,
                None
              )
            )
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
