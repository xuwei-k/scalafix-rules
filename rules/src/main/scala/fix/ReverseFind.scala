package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

private object ReverseFind {
  private val ScalaCollectionSeq: Set[String] = Set(
    "scala/collection/immutable/Seq#",
    "scala/collection/immutable/List#",
    "scala/collection/immutable/Vector#",
    "scala/collection/immutable/LazyList#",
    "scala/collection/mutable/Seq#",
    "scala/collection/Seq#",
    "scala/Array#",
  )
}

class ReverseFind extends SemanticRule("ReverseFind") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            x @ Term.Select(
              Term.Select(
                obj,
                reverse @ Term.Name("reverse")
              ),
              find @ Term.Name("find")
            ),
            Term.ArgClause(
              _ :: Nil,
              None
            )
          ) =>
        obj.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(tpe: TypeRef) if ReverseFind.ScalaCollectionSeq(tpe.symbol.value) =>
              Seq(
                Patch.removeTokens(reverse.tokens),
                x.tokens.collectFirst {
                  case t if t.is[Token.Dot] && (obj.pos.end <= t.pos.start) && (t.pos.start <= reverse.pos.start) =>
                    Patch.removeToken(t)
                }.asPatch,
                Patch.replaceTree(find, "findLast")
              ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
