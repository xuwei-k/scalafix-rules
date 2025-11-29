package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.v1.MethodSignature
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionTreeScalafix

private object SortedMaxMin {
  object HeadOrLastSorted {
    def unapply(x: String): Option[String] = PartialFunction.condOpt(x) {
      case "head" => "min"
      case "last" => "max"
      case "headOption" => "minOption"
      case "lastOption" => "maxOption"
    }
  }

  object HeadOrLastSortBy {
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

class SortedMaxMin extends SemanticRule("SortedMaxMin") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t1 @ Term.Select(
            t2 @ Term.Select(
              _,
              sorted @ Term.Name("sorted")
            ),
            headOrLast @ Term.Name(SortedMaxMin.HeadOrLastSorted(newMethod))
          ) =>
        sorted.symbol.info
          .map(_.signature)
          .collect {
            case MethodSignature(_, List(x :: Nil), _) if SortedMaxMin.sortedValues(x.symbol.owner.value) =>
              Seq(
                Patch.replaceTree(sorted, newMethod),
                t1.tokens
                  .find(n => n.is[Token.Dot] && (n.pos.start >= t2.pos.end) && (n.pos.end <= headOrLast.pos.start))
                  .map(Patch.removeToken)
                  .asPatch,
                Patch.removeTokens(headOrLast.tokens)
              ).asPatch
          }
          .asPatch
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
            headOrLast @ Term.Name(SortedMaxMin.HeadOrLastSortBy(newMethod))
          ) =>
        sortBy.symbol.info
          .map(_.signature)
          .collect {
            case MethodSignature(
                  _,
                  List(x1 :: Nil, _),
                  _
                ) if SortedMaxMin.sortByValues(x1.symbol.owner.value) =>
              Seq(
                Patch.replaceTree(sortBy, newMethod),
                t1.tokens
                  .find(n => n.is[Token.Dot] && (n.pos.start >= t2.pos.end) && (n.pos.end <= headOrLast.pos.start))
                  .map(Patch.removeToken)
                  .asPatch,
                Patch.removeTokens(headOrLast.tokens)
              ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
