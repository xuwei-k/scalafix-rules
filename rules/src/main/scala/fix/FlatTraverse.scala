package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class FlatTraverse extends SyntacticRule("FlatTraverse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.Initial(
            Term.Select(
              Term.Apply.Initial(
                Term.Select(
                  qual,
                  Term.Name("traverse")
                ),
                arg :: Nil
              ),
              Term.Name("map")
            ),
            a @ Term.AnonymousFunction(
              Term.Select(
                Term.Placeholder(),
                Term.Name("flatten")
              )
            ) :: Nil
          ) =>
        if (a.toString.trim.startsWith("{")) {
          Patch.replaceTree(t, s"${qual}.flatTraverse${arg}")
        } else {
          Patch.replaceTree(t, s"${qual}.flatTraverse(${arg})")
        }
    }
  }.asPatch
}
