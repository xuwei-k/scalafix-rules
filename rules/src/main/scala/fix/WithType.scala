package fix

import scala.meta.Decl
import scala.meta.Term
import scala.meta.Type
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.tokens.Token

class WithType extends SyntacticRule("WithType") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Type.With
          if t.lhs.collect { case _: Type.With =>
            ()
          }.isEmpty &&
            t.rhs.collect { case _: Type.With =>
              ()
            }.isEmpty =>
        t.tokens
          .find(_.is[Token.KwWith])
          .map { withToken =>
            Patch.replaceToken(withToken, "&")
          }
          .asPatch
    }.asPatch
  }
}
