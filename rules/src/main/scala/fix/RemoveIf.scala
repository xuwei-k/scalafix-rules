package fix

import scala.meta.Lit
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object RemoveIf {
  private object True {
    def unapply(t: Term): Boolean = PartialFunction.cond(t) {
      case Lit.Boolean(true) =>
        true
      case Term.Block(Lit.Boolean(true) :: Nil) =>
        true
    }
  }

  private object False {
    def unapply(t: Term): Boolean = PartialFunction.cond(t) {
      case Lit.Boolean(false) =>
        true
      case Term.Block(Lit.Boolean(false) :: Nil) =>
        true
    }
  }
}

class RemoveIf extends SyntacticRule("RemoveIf") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case x @ Term.If.After_4_4_0(
            cond,
            RemoveIf.True(),
            RemoveIf.False(),
            _
          ) =>
        Patch.replaceTree(x, cond.toString)
      case x @ Term.If.After_4_4_0(cond, RemoveIf.False(), RemoveIf.True(), _) =>
        cond match {
          case Term.ApplyInfix.After_4_6_0(
                a1,
                Term.Name("=="),
                _,
                Term.ArgClause(
                  a2 :: Nil,
                  None
                )
              ) =>
            Patch.replaceTree(x, s"${a1} != ${a2}")
          case Term.ApplyInfix.After_4_6_0(
                a1,
                Term.Name("!="),
                _,
                Term.ArgClause(
                  a2 :: Nil,
                  None
                )
              ) =>
            Patch.replaceTree(x, s"${a1} == ${a2}")
          case Term.Select(
                a1,
                Term.Name("isEmpty")
              ) =>
            Patch.replaceTree(x, s"${a1}.nonEmpty")
          case Term.Select(
                a1,
                Term.Name("nonEmpty")
              ) =>
            Patch.replaceTree(x, s"${a1}.isEmpty")
          case _ =>
            Patch.replaceTree(x, s"!($cond)")
        }
    }.asPatch
  }
}
