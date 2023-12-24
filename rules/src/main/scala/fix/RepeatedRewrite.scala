package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.tokens.Token

class RepeatedRewrite extends SyntacticRule("RepeatedRewrite") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.Repeated(x) =>
      val a = t.tokens.dropWhile(!_.is[Token.Colon])
      if (a.nonEmpty) {
        a.dropWhile(!_.is[Token.Underscore]).drop(1).headOption match {
          case Some(Token.Ident("*")) =>
            Seq(
              if (x.is[Term.ApplyInfix]) {
                Patch.addAround(x, "(", ")")
              } else {
                Patch.empty
              },
              Patch.removeToken(a.head),
              a.find(_.is[Token.Underscore]).map(Patch.removeToken).asPatch,
            ).asPatch
          case _ =>
            Patch.empty
        }
      } else {
        Patch.empty
      }
    }.asPatch
  }
}
