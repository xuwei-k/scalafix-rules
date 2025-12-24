package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class FilterSize extends SyntacticRule("FilterSize") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(
                obj,
                Term.Name("filter")
              ),
              Term.ArgClause(
                f :: Nil,
                None
              )
            ),
            Term.Name("size" | "length")
          ) =>
        Patch.replaceTree(t, s"${obj}.count($f)")
    }.asPatch
  }
}
