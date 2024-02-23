package fix

import scala.meta.Enumerator
import scala.meta.Term
import scala.meta.Term.ApplyType
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class RemovePureEff extends SyntacticRule("RemovePureEff") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case x1: Term.ForYield =>
      x1.enums
        .drop(1)
        .collect {
          case x @ Enumerator.Generator(
                _,
                ApplyType.Initial(Term.Select(rhs, Term.Name("pureEff")), Type.Name(_) :: Nil)
              ) =>
            Patch.replaceTree(x, Enumerator.Val(x.pat, rhs).toString)
          case x @ Enumerator.Generator(
                _,
                Term.Apply.After_4_6_0(
                  Term.ApplyType.After_4_6_0(
                    Term.Select(
                      Term.Name("Eff"),
                      Term.Name("pure")
                    ),
                    Type.ArgClause(_ :: _ :: Nil)
                  ),
                  Term.ArgClause(arg :: Nil, None)
                )
              ) =>
            Patch.replaceTree(x, Enumerator.Val(x.pat, arg).toString)

          case x @ Enumerator.Generator(
                _,
                Term.Apply.After_4_6_0(
                  Term.Select(
                    Term.Name("Eff"),
                    Term.Name("pure")
                  ),
                  Term.ArgClause(arg :: Nil, None)
                )
              ) =>
            Patch.replaceTree(x, Enumerator.Val(x.pat, arg).toString)
        }
        .asPatch
    }.asPatch
  }
}
