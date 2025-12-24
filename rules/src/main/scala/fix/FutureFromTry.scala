package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.transversers._
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

private object FutureFromTry {
  private object S {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.After_4_6_0(
              Term.Name("Success"),
              Pat.ArgClause(
                Pat.Var(Term.Name(x1)) :: Nil
              )
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Future") | Term.Select(
                Term.Name("Future"),
                Term.Name("successful")
              ),
              Term.ArgClause(
                Term.Name(x2) :: Nil,
                None
              )
            )
          ) =>
        x1 == x2
    }
  }

  private object F {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.After_4_6_0(
              Term.Name("Failure"),
              Pat.ArgClause(
                Pat.Var(Term.Name(x1)) :: Nil
              )
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Select(
                Term.Name("Future"),
                Term.Name("failed")
              ),
              Term.ArgClause(
                Term.Name(x2) :: Nil,
                None
              )
            )
          ) =>
        x1 == x2
    }
  }
}

class FutureFromTry extends SyntacticRule("FutureFromTry") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_9_9(
            x,
            Term.CasesBlock(
              (FutureFromTry.S() :: FutureFromTry.F() :: Nil) | (FutureFromTry.F() :: FutureFromTry.S() :: Nil)
            ),
            Nil
          ) =>
        Patch.replaceTree(t, s"Future.fromTry($x)")
    }.asPatch
  }
}
