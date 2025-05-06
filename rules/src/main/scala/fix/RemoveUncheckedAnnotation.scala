package fix

import scala.meta.Init
import scala.meta.Mod
import scala.meta.Name
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class RemoveUncheckedAnnotation extends SyntacticRule("RemoveUncheckedAnnotation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Annotate(
          _,
          List(
            a @ Mod.Annot(
              Init.After_4_6_0(
                Type.Name("unchecked"),
                Name.Anonymous(),
                Nil
              )
            )
          )
        ) =>
        Seq(
          Patch.removeToken(
            t.tokens.filter(_.is[scala.meta.tokens.Token.Colon]).last
          ),
          Patch.removeTokens(a.tokens)
        ).asPatch
    }.asPatch
  }
}

