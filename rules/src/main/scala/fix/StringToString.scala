package fix

import fix.StringToString.BlockLastOrSelf
import scala.meta.Stat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

private object StringToString {
  private object BlockLastOrSelf {
    def unapply(t: Term): Option[Stat] = PartialFunction.condOpt(t) {
      case Term.Block(_ :+ last) =>
        last
      case other =>
        other
    }
  }

  private val stringTypes: String => Boolean = Set(
    "java.lang.String.",
    "scala.Predef.String."
  )
}

class StringToString extends SemanticRule("StringToString") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            BlockLastOrSelf(a),
            s @ Term.Name("toString")
          ) =>
        a.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(tpe: TypeRef) if StringToString.stringTypes(tpe.symbol.normalized.value) =>
              Seq(
                Patch.removeTokens(s.tokens),
                t.tokens.reverseIterator.find(_.is[Token.Dot]).map(Patch.removeToken).asPatch
              ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
