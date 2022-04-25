package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.tokens.Token

class UnnecessaryCase extends SyntacticRule("UnnecessaryCase") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.PartialFunction(Case(Pat.Var(_), None, _) :: Nil) =>
      t.tokens.find(_.is[Token.KwCase]).fold(Patch.empty)(Patch.removeToken)
    }.asPatch
  }
}
