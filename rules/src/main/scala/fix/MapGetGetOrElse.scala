package fix

import scala.annotation.nowarn
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.tokens.Token
import scala.meta.transversers._
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

object MapGetGetOrElse {
  @nowarn("msg=AnyRefMap")
  private val map2Types: Set[String] = (Set[Class[?]](
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
  ).map(_.getName) ++ Set(
    "scala.Predef.Map"
  )).map(_ + ".")

  private val map1Types: Set[String] = Set[Class[?]](
    classOf[scala.collection.immutable.IntMap[?]],
    classOf[scala.collection.immutable.LongMap[?]],
  ).map(_.getName + ".")

  private object IsMapType {
    def unapply(t: Term)(implicit doc: SemanticDocument): Boolean = {
      t.symbol.info
        .map(_.signature)
        .collect {
          case ValueSignature(TypeRef(_, sym, _ :: _ :: Nil)) if map2Types.contains(sym.normalized.value) =>
            ()
          case ValueSignature(TypeRef(_, sym, _ :: Nil)) if map1Types.contains(sym.normalized.value) =>
            ()
        }
        .isDefined
    }
  }
}

class MapGetGetOrElse extends SemanticRule("MapGetGetOrElse") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(
              Term.Apply.After_4_6_0(
                mapGet @ Term.Select(
                  map @ MapGetGetOrElse.IsMapType(),
                  get @ Term.Name("get")
                ),
                keyArg @ Term.ArgClause(
                  key :: Nil,
                  None
                )
              ),
              Term.Name("getOrElse")
            ),
            Term.ArgClause(
              default :: Nil,
              None
            )
          ) =>
        Seq(
          mapGet.tokens.collectFirst {
            case t if t.is[Token.Dot] && (map.pos.end <= t.pos.start) && (t.pos.end <= get.pos.start) =>
              Patch.removeToken(t)
          }.asPatch,
          Patch.removeTokens(get.tokens),
          Patch.removeTokens(keyArg.tokens),
          Patch.addLeft(default, s"${key}, ")
        ).asPatch
    }.asPatch
  }
}
