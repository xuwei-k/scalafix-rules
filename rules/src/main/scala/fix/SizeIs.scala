package fix

import scala.meta.Lit
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionTreeScalafix

private object SizeIs {
  private val ScalaCollectionLength: Set[String] = Set(
    "scala/package.Seq#",
    "scala/package.Range#",
    "scala/package.List#",
    "scala/package.Vector#",
    "scala/package.LazyList#",
    "scala/collection/immutable/Seq#",
    "scala/collection/immutable/List#",
    "scala/collection/immutable/Vector#",
    "scala/collection/immutable/LazyList#",
    "scala/collection/mutable/Seq#",
    "scala/collection/Seq#",
  )
  private val ScalaCollectionSize: Set[String] = Set(
    "scala/Predef.Map#",
    "scala/Predef.Set#",
    "scala/collection/immutable/Map#",
    "scala/collection/immutable/Set#",
    "scala/collection/mutable/Map#",
    "scala/collection/mutable/Set#",
    "scala/collection/concurrent/TrieMap#",
    "scala/collection/Map#",
    "scala/collection/Set#",
  ) ++ ScalaCollectionLength

  private object IntType {
    def unapply(t: Term)(implicit doc: SemanticDocument): Boolean = PartialFunction.cond(t) {
      case _: Lit.Int =>
        true
      case _ =>
        t.symbol.info
          .map(_.signature)
          .collect { case ValueSignature(tpe: TypeRef) if tpe.symbol.value == "scala/Int#" => () }
          .nonEmpty
    }
  }
}

class SizeIs extends SemanticRule("SizeIs") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.ApplyInfix.After_4_6_0(
            Term.Select(
              obj,
              method @ Term.Name("size" | "length")
            ),
            Term.Name("<" | "<=" | ">" | ">=" | "==" | "!="),
            Type.ArgClause(Nil),
            Term.ArgClause(
              List(
                SizeIs.IntType()
              ),
              None
            )
          ) =>
        obj.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(tpe: TypeRef)
                if SizeIs.ScalaCollectionLength(tpe.symbol.value) && (method.value == "length") =>
              Patch.replaceTree(method, "lengthIs")
            case ValueSignature(tpe: TypeRef)
                if SizeIs.ScalaCollectionSize(tpe.symbol.value) && (method.value == "size") =>
              Patch.replaceTree(method, "sizeIs")
          }
          .asPatch
    }.asPatch
  }
}
