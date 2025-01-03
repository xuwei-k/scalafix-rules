package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

class MatchParentheses extends SyntacticRule("MatchParentheses") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Term.Match =>
      PartialFunction
        .condOpt(doc.tokens.filter(_.pos.start >= t.pos.end).take(2)) { case Seq(r: Token.RightParen, _: Token.Dot) =>
          PartialFunction
            .condOpt(doc.tokens.filter(_.pos.end <= t.pos.start).takeRight(2)) {
              case Seq(n, l: Token.LeftParen) if !n.is[Token.Ident] =>
                Seq(
                  Patch.removeToken(l),
                  t.tokens.collectFirst { case m: Token.KwMatch =>
                    Patch.addLeft(m, ".")
                  }.asPatch,
                  Patch.removeToken(r)
                ).asPatch
            }
            .asPatch
        }
        .asPatch
    }.asPatch
  }
}
