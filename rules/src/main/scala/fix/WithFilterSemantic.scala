package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

private object WithFilterSemantic {
  private val filterValues: Set[String] = Set(
    "scala/collection/TraversableLike",
    "scala/collection/IterableOps",
    "scala/collection/immutable/List",
    "scala/collection/immutable/LazyList",
    "scala/collection/immutable/Stream",
    "scala/collection/StrictOptimizedIterableOps"
  ).map(_ + "#filter().")
}

class WithFilterSemantic extends SemanticRule("WithFilterSemantic") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(
              Term.Apply.After_4_6_0(
                Term.Select(_, filter @ Term.Name("filter")),
                Term.ArgClause(_ :: Nil, _)
              ),
              Term.Name("map" | "flatMap" | "foreach")
            ),
            Term.ArgClause(_ :: Nil, _)
          ) if WithFilterSemantic.filterValues(filter.symbol.value) =>
        Patch.replaceTree(filter, "withFilter")
    }.asPatch
  }
}
