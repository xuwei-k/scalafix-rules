package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class OptionFilter extends SyntacticRule("OptionFilter") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(
            expr,
            List(
              Case(
                Pat.Extract.Initial(Term.Name("Some"), Pat.Var(Term.Name(a1)) :: Nil),
                Some(condition),
                Term.Apply.Initial(Term.Name("Some"), Term.Name(a2) :: Nil)
              ),
              Case(
                Pat.Wildcard(),
                None,
                Term.Name("None")
              )
            ),
            _
          ) if a1 == a2 =>
        Patch.replaceTree(t, s"${expr}.filter(${a1} => ${condition})")
    }.asPatch
  }
}
