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

class SizeToLength extends SemanticRule("SizeToLength") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(
            obj,
            size @ Term.Name("size")
          ) =>
        val p = Patch.replaceTree(size, "length")
        obj.symbol.info.flatMap { i =>
          PartialFunction.condOpt(i.signature) {
            case ValueSignature(TypeRef(_, symbol, Nil)) if symbol.normalized.value == "scala.Predef.String." =>
              p
            case ValueSignature(TypeRef(_, symbol, _ :: Nil)) if symbol.normalized.value == "scala.Array." =>
              p
          }
        }.asPatch
    }.asPatch
  }
}
