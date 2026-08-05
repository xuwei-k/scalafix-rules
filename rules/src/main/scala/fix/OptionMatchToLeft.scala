package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class OptionMatchToLeft extends SyntacticRule("OptionMatchToLeft") {
  private object SomeToLeft {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.After_4_6_0(Term.Name("Some"), Pat.ArgClause(Pat.Var(Term.Name(a1)) :: Nil)),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Left"),
              Term.ArgClause(
                Term.Name(a2) :: Nil,
                None
              )
            )
          ) =>
        a1 == a2
    }
  }

  private object NoneToRight {
    def unapply(c: Case): Option[Term] = PartialFunction.condOpt(c) {
      case Case(
            Term.Name("None") | Pat.Wildcard(),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Right"),
              Term.ArgClause(
                leftArg :: Nil,
                None
              )
            )
          ) =>
        leftArg
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(expr, SomeToLeft() :: NoneToRight(leftArg) :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.toLeft(${leftArg})")
      case t @ Term.Match.After_4_4_5(expr, NoneToRight(leftArg) :: SomeToLeft() :: Nil, _) =>
        Patch.replaceTree(t, s"${expr}.toLeft(${leftArg})")
    }
  }.asPatch
}
