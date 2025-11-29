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
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionTreeScalafix

private object FilterHeadOption {
  private val filterValues: Set[String] = Set(
    "scala/collection/TraversableLike#filter().",
    "scala/collection/IterableOps#filter().",
    "scala/collection/immutable/List#filter().",
    "scala/collection/immutable/LazyList#filter().",
    "scala/collection/immutable/Stream#filter().",
    "scala/collection/StrictOptimizedIterableOps#filter()."
  )
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
          ) =>
        PartialFunction
          .condOpt((filter.symbol.info.map(_.signature), headOption.symbol.info.map(_.signature))) {
            case (Some(MethodSignature(Nil, List(x :: Nil), _)), Some(MethodSignature(Nil, Nil, _)))
                if FilterHeadOption.filterValues(x.symbol.owner.value) =>
              x.signature match {
                case ValueSignature(tpe: TypeRef) if tpe.symbol.value == "scala/Function1#" =>
                  Seq(
                    Patch.replaceTree(filter, "find"),
                    t1.tokens
                      .find(n => n.is[Token.Dot] && (n.pos.start >= t2.pos.end) && (n.pos.end <= headOption.pos.start))
                      .map(Patch.removeToken)
                      .asPatch,
                    Patch.removeTokens(headOption.tokens)
                  ).asPatch
                case _ =>
                  Patch.empty
              }
          }
          .asPatch
    }.asPatch
  }
}
