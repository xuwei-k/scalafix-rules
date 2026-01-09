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

class FindExists extends SemanticRule("FindExists") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            applyFind @ Term.Apply.After_4_6_0(
              Term.Select(
                obj,
                find @ Term.Name("find")
              ),
              Term.ArgClause(
                _ :: Nil,
                None
              )
            ),
            method @ Term.Name("isDefined" | "nonEmpty")
          ) =>
        obj.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(tpe: TypeRef)
                if Seq(
                  "scala/collection/",
                  "scala/package.",
                  "scala/Array#",
                  "scala/Predef."
                ).exists(tpe.symbol.value.startsWith) =>
              Seq(
                Patch.replaceTree(find, "exists"),
                Patch.removeTokens(method.tokens),
                t.tokens.reverseIterator
                  .find(_.is[Token.Dot])
                  .filter(_.pos.start >= applyFind.pos.end)
                  .map(Patch.removeToken)
                  .asPatch
              ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
