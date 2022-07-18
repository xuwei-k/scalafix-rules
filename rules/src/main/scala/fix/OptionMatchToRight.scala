package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term

class OptionMatchToRight extends SyntacticRule("OptionMatchToRight") {
  private object SomeToRight {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract(Term.Name("Some"), Pat.Var(Term.Name(a1)) :: Nil),
            None,
            Term.Apply(Term.Name("Right"), Term.Name(a2) :: Nil)
          ) if a1 == a2 =>
        true

    }
  }

  private object NoneToLeft {
    def unapply(c: Case): Option[Term] = PartialFunction.condOpt(c) {
      case Case(
            Term.Name("None") | Pat.Wildcard(),
            None,
            Term.Apply(Term.Name("Left"), leftArg :: Nil)
          ) =>
        leftArg
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match(expr, SomeToRight() :: NoneToLeft(leftArg) :: Nil) =>
        Patch.replaceTree(t, s"${expr}.toRight(${leftArg})")
      case t @ Term.Match(expr, NoneToLeft(leftArg) :: SomeToRight() :: Nil) =>
        Patch.replaceTree(t, s"${expr}.toRight(${leftArg})")
    }
  }.asPatch
}
