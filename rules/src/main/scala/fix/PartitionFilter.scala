package fix

import fix.PartitionFilter.FilterType
import scala.meta.Defn
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

private object PartitionFilter {
  private val ScalaCollection: Set[String] = Set(
    "scala/package.Seq#",
    "scala/package.Range#",
    "scala/package.List#",
    "scala/package.Vector#",
    "scala/Predef.Set#",
    "scala/Predef.Map#",
    "scala/collection/immutable/Set#",
    "scala/collection/immutable/Map#",
    "scala/collection/immutable/Seq#",
    "scala/collection/immutable/List#",
    "scala/collection/immutable/Vector#",
    "scala/collection/immutable/LazyList#",
    "scala/collection/mutable/Seq#",
    "scala/collection/mutable/Set#",
    "scala/collection/mutable/Map#",
    "scala/collection/Seq#",
    "scala/collection/Set#",
    "scala/collection/Map#",
    "scala/Array#",
  )

  private final case class FilterType(res: String, method: String)

  private object FilterType {
    def unapply(p: Pat): Option[FilterType] = PartialFunction.condOpt(p) {
      case Pat.Tuple(Pat.Var(res: Term.Name) :: Pat.Wildcard() :: Nil) =>
        FilterType(res.value, "filter")
      case Pat.Tuple(Pat.Wildcard() :: Pat.Var(res: Term.Name) :: Nil) =>
        FilterType(res.value, "filterNot")
    }
  }
}

class PartitionFilter extends SemanticRule("PartitionFilter") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Defn.Val(
            _,
            List(
              tuple @ FilterType(value)
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Select(
                obj,
                partition @ Term.Name("partition")
              ),
              Term.ArgClause(
                _ :: Nil,
                None
              )
            )
          ) =>
        obj.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(tpe: TypeRef) if PartitionFilter.ScalaCollection(tpe.symbol.value) =>
              Seq(
                Patch.replaceTree(tuple, value.res),
                Patch.replaceTree(partition, value.method)
              ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
