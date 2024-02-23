package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UnnecessaryCase extends SyntacticRule("UnnecessaryCase") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.PartialFunction(Case(Pat.Var(_) | Pat.Wildcard(), None, _) :: Nil) =>
      t.tokens.find(_.is[Token.KwCase]).fold(Patch.empty)(Patch.removeToken)
    }.asPatch
  }
}
