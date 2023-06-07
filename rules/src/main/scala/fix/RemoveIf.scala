package fix

import scala.meta.Lit
import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

object RemoveIf {
  object True {
    def unapply(t: Term): Boolean = PartialFunction.cond(t) {
      case Lit.Boolean(true) =>
        true
      case Term.Block(Lit.Boolean(true) :: Nil) =>
        true
    }
  }

  object False {
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
          case Term.ApplyInfix.Initial(
                a1,
                Term.Name("=="),
                _,
                a2 :: Nil
              ) =>
            Patch.replaceTree(x, s"${a1} != ${a2}")
          case Term.ApplyInfix.Initial(
                a1,
                Term.Name("!="),
                _,
                a2 :: Nil
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
