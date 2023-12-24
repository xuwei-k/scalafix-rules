package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.tokens.Token

class EtaExpansionRewrite extends SyntacticRule("EtaExpansionRewrite") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.Eta(x) =>
      t.tokens.reverseIterator.find(_.is[Token.Underscore]).filter(_.start > x.pos.end).map(Patch.removeToken).asPatch
    }.asPatch
  }
}
