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
                Pat.Extract.After_4_6_0(Term.Name("Some"), Pat.ArgClause(Pat.Var(Term.Name(a1)) :: Nil)),
                Some(condition),
                Term.Apply.After_4_6_0(
                  Term.Name("Some"),
                  Term.ArgClause(
                    Term.Name(a2) :: Nil,
                    None
                  )
                )
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
