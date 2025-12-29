package fix

import fix.ForeachEntry.IsMapType
import scala.annotation.nowarn
import scala.meta.Case
import scala.meta.Enumerator
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionTreeScalafix

object ForeachEntry {
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
          case ValueSignature(TypeRef(_, sym, _ :: _ :: Nil))
              if ForeachEntry.map2Types.contains(sym.normalized.value) =>
            ()
          case ValueSignature(TypeRef(_, sym, _ :: Nil)) if ForeachEntry.map1Types.contains(sym.normalized.value) =>
            ()
        }
        .isDefined
    }
  }
}

class ForeachEntry extends SemanticRule("ForeachEntry") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(
              IsMapType(),
              foreach @ Term.Name("foreach")
            ),
            Term.ArgClause(
              List(
                Term.PartialFunction(
                  List(
                    case1 @ Case(
                      Pat.Tuple(
                        List(
                          Pat.Var(_: Term.Name) | Pat.Wildcard(),
                          Pat.Var(_: Term.Name) | Pat.Wildcard()
                        )
                      ),
                      None,
                      _
                    )
                  )
                )
              ),
              None
            )
          ) =>
        Seq(
          Patch.replaceTree(foreach, "foreachEntry"),
          case1.tokens.find(_.is[Token.KwCase]).map(Patch.removeToken).asPatch
        ).asPatch
      case Term.For.After_4_9_9(
            e @ Term.EnumeratorsBlock(
              List(
                Enumerator.Generator(
                  Pat.Tuple(
                    List(
                      Pat.Var(_: Term.Name) | Pat.Wildcard(),
                      Pat.Var(_: Term.Name) | Pat.Wildcard()
                    )
                  ),
                  IsMapType()
                )
              )
            ),
            _
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "use foreachEntry",
            position = e.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
