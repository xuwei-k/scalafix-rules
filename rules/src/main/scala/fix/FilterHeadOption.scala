package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionTreeScalafix

private object FilterHeadOption {
  private val filterValues: Set[String] = Set(
    "scala/collection/TraversableLike",
    "scala/collection/IterableOps",
    "scala/collection/immutable/List",
    "scala/collection/immutable/LazyList",
    "scala/collection/immutable/Stream",
    "scala/collection/StrictOptimizedIterableOps"
  ).map(_ + "#filter().")

  private val headOptionValues: Set[String] = Set(
    "scala/collection/TraversableLike",
    "scala/collection/IterableOps",
    "scala/collection/LinearSeqOps",
    "scala/collection/IndexedSeqOps",
  ).map(_ + "#headOption().")
}

class FilterHeadOption extends SemanticRule("FilterHeadOption") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t1 @ Term.Select(
            t2 @ Term.Apply.After_4_6_0(
              Term.Select(
                _,
                filter @ Term.Name("filter")
              ),
              Term.ArgClause(
                _ :: Nil,
                None
              )
            ),
            headOption @ Term.Name("headOption")
          )
          if FilterHeadOption.filterValues(
            filter.symbol.value
          ) && FilterHeadOption.headOptionValues(
            headOption.symbol.value
          ) =>

        Seq(
          Patch.replaceTree(filter, "find"),
          t1.tokens
            .find(n => n.is[Token.Dot] && (n.pos.start >= t2.pos.end) && (n.pos.end <= headOption.pos.start))
            .map(Patch.removeToken)
            .asPatch,
          Patch.removeTokens(headOption.tokens)
        ).asPatch
    }.asPatch
  }
}
