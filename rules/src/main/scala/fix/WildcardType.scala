package fix

import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class WildcardType extends SyntacticRule("WildcardType") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Type.Wildcard =>
      t.tokens.find(_.is[Token.Underscore]).map(x => Patch.replaceToken(x, "?")).asPatch
    }.asPatch
  }
}
