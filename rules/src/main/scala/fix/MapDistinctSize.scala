package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class MapDistinctSize extends SyntacticRule("MapDistinctSize") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Select(
              Term.Apply.After_4_6_0(
                Term.Select(
                  x,
                  Term.Name("map")
                ),
                Term.ArgClause(
                  fun :: Nil,
                  None
                )
              ),
              Term.Name("distinct")
            ),
            Term.Name(s @ ("size" | "sizeIs" | "length" | "lengthIs"))
          ) =>
        Patch.replaceTree(t, s"${x}.distinctBy(${fun}).${s}")
    }
  }.asPatch
}
