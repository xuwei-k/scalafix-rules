package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

class ReplaceMapContains extends SemanticRule("ReplaceMapContains") {
  private[this] val map2Types: Set[String] = Set[Class[?]](
    classOf[scala.collection.Map[?, ?]],
    classOf[scala.collection.immutable.Map[?, ?]],
    classOf[scala.collection.immutable.HashMap[?, ?]],
    classOf[scala.collection.immutable.TreeMap[?, ?]],
    classOf[scala.collection.immutable.SortedMap[?, ?]],
    classOf[scala.collection.mutable.Map[?, ?]],
    classOf[scala.collection.mutable.HashMap[?, ?]],
    classOf[scala.collection.mutable.TreeMap[?, ?]],
    classOf[scala.collection.mutable.SortedMap[?, ?]],
    classOf[scala.collection.mutable.AnyRefMap[?, ?]],
    classOf[scala.collection.concurrent.TrieMap[?, ?]],
  ).map(_.getName + ".")

  private[this] val map1Types: Set[String] = Set[Class[?]](
    classOf[scala.collection.immutable.IntMap[?]],
    classOf[scala.collection.immutable.LongMap[?]],
  ).map(_.getName + ".")

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(
                map,
                Term.Name("get")
              ),
              Term.ArgClause(
                arg :: Nil,
                None
              )
            ),
            method @ Term.Name("isDefined" | "isEmpty" | "nonEmpty")
          ) =>

        val p = method.value match {
          case "isEmpty" =>
            Patch.replaceTree(t, s"!${map}.contains($arg)")
          case _ =>
            Patch.replaceTree(t, s"${map}.contains($arg)")
        }

        map.symbol.info.flatMap { i =>
          PartialFunction.condOpt(i.signature) {
            case ValueSignature(TypeRef(_, sym, _ :: _ :: Nil)) if map2Types.contains(sym.normalized.value) =>
              p
            case ValueSignature(TypeRef(_, sym, _ :: Nil)) if map1Types.contains(sym.normalized.value) =>
              p
          }
        }.asPatch
    }.asPatch
  }
}
