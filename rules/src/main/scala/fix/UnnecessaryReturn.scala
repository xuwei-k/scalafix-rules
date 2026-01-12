package fix

import fix.UnnecessaryReturn.BlockLastOrSelf
import scala.meta.Defn
import scala.meta.Stat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token.KwReturn
import scala.meta.tokens.Token.Whitespace
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

private object UnnecessaryReturn {
  private object BlockLastOrSelf {
    def unapply(t: Term): Option[Stat] = PartialFunction.condOpt(t) {
      case Term.Block(_ :+ last) =>
        last
      case other =>
        other
    }
  }
}

class UnnecessaryReturn extends SyntacticRule("UnnecessaryReturn") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Defn.Def =>
      PartialFunction
        .condOpt(t.body) {
          case BlockLastOrSelf(returnTerm: Term.Return) =>
            removeReturn(returnTerm)
          case BlockLastOrSelf(t: Term.MatchLike) =>
            t.cases
              .map(_.body)
              .collect { case BlockLastOrSelf(returnTerm: Term.Return) =>
                removeReturn(returnTerm)
              }
              .asPatch
          case BlockLastOrSelf(t: Term.If) =>
            Seq(
              PartialFunction
                .condOpt(t.thenp) { case BlockLastOrSelf(returnTerm: Term.Return) =>
                  removeReturn(returnTerm)
                }
                .asPatch,
              PartialFunction
                .condOpt(t.elsep) { case BlockLastOrSelf(returnTerm: Term.Return) =>
                  removeReturn(returnTerm)
                }
                .asPatch
            ).asPatch
        }
        .asPatch
    }.asPatch
  }

  private def removeReturn(returnTerm: Term.Return): Patch = {
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
}
