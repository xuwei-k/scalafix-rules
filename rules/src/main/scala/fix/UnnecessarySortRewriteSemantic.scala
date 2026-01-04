package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.rule.RuleName
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionTreeScalafix

private object UnnecessarySortRewriteSemantic {
  private object HeadOrLastSorted {
    def unapply(x: String): Option[String] = PartialFunction.condOpt(x) {
      case "head" => "min"
      case "last" => "max"
      case "headOption" => "minOption"
      case "lastOption" => "maxOption"
    }
  }

  private object HeadOrLastSortBy {
    def unapply(x: String): Option[String] = PartialFunction.condOpt(x) {
      case "head" => "minBy"
      case "last" => "maxBy"
      case "headOption" => "minByOption"
      case "lastOption" => "maxByOption"
    }
  }

  private val sortedValues: Set[String] = Set(
    "scala/collection/SeqOps#sorted().",
    "scala/collection/immutable/StrictOptimizedSeqOps#sorted().",
    "scala/collection/SeqLike#sorted().",
  )

  private val sortByValues: Set[String] = Set(
    "scala/collection/SeqOps#sortBy().",
    "scala/collection/SeqLike#sortBy().",
  )
}

class UnnecessarySortRewriteSemantic
    extends SemanticRule(
      RuleName("UnnecessarySortRewriteSemantic").withDeprecatedName(
        "SortedMaxMin",
        "Use UnnecessarySortRewriteSemantic instead",
        "0.6.20"
      )
    ) {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t1 @ Term.Select(
            t2 @ Term.Select(
              _,
              sorted @ Term.Name("sorted")
            ),
            headOrLast @ Term.Name(UnnecessarySortRewriteSemantic.HeadOrLastSorted(newMethod))
          ) if UnnecessarySortRewriteSemantic.sortedValues(sorted.symbol.value) =>
        Seq(
          Patch.replaceTree(sorted, newMethod),
          t1.tokens
            .find(n => n.is[Token.Dot] && (n.pos.start >= t2.pos.end) && (n.pos.end <= headOrLast.pos.start))
            .map(Patch.removeToken)
            .asPatch,
          Patch.removeTokens(headOrLast.tokens)
        ).asPatch
      case t1 @ Term.Select(
            t2 @ Term.Apply.After_4_6_0(
              Term.Select(
                _,
                sortBy @ Term.Name("sortBy")
              ),
              Term.ArgClause(
                _ :: Nil,
                None
              )
            ),
            headOrLast @ Term.Name(UnnecessarySortRewriteSemantic.HeadOrLastSortBy(newMethod))
          ) if UnnecessarySortRewriteSemantic.sortByValues(sortBy.symbol.value) =>
        Seq(
          Patch.replaceTree(sortBy, newMethod),
          t1.tokens
            .find(n => n.is[Token.Dot] && (n.pos.start >= t2.pos.end) && (n.pos.end <= headOrLast.pos.start))
            .map(Patch.removeToken)
            .asPatch,
          Patch.removeTokens(headOrLast.tokens)
        ).asPatch
    }.asPatch
  }
}
