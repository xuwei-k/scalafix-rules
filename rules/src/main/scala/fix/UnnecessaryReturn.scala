package fix

import scala.meta.Defn
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token.KwReturn
import scala.meta.tokens.Token.Whitespace
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class UnnecessaryReturn extends SyntacticRule("UnnecessaryReturn") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Defn.Def =>
      PartialFunction
        .condOpt(t.body) {
          case Term.Block(
                _ :+ (returnTerm: Term.Return)
              ) =>
            returnTerm.tokens
              .zip(returnTerm.tokens.drop(1))
              .collectFirst { case (r: KwReturn, s) =>
                Seq(
                  Patch.removeToken(r),
                  PartialFunction
                    .condOpt(s) { case _: Whitespace =>
                      Patch.removeToken(s)
                    }
                    .asPatch
                ).asPatch
              }
              .asPatch
        }
        .asPatch
    }.asPatch
  }
}
