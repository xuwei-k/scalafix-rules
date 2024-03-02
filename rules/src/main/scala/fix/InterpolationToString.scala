package fix

import scala.meta.Term
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scala.meta.classifiers._
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

class InterpolationToString extends SyntacticRule("InterpolationToString") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Interpolate(
            Term.Name("s"),
            _,
            values
          ) =>
        values.collect {
          case x1 @ InterpolationToString.SelectToString(x2) =>
            (x1, x2)
          case x1 @ Term.Block(InterpolationToString.SelectToString(x2) :: Nil) =>
            (x1, x2)
        }.map { case (x1, x2) =>
          Seq(
            x1.tokens.reverseIterator
              .find(t => t.is[Token.Dot] && (t.pos.start < x2.pos.start))
              .map(Patch.removeToken)
              .asPatch,
            Patch.removeTokens(x2.tokens)
          ).asPatch
        }.asPatch
    }
  }.asPatch
}

private object InterpolationToString {
  private object SelectToString {
    def unapply(t: Tree): Option[Term] = PartialFunction.condOpt(t) {
      case Term.Select(
            _,
            x @ Term.Name("toString")
          ) =>
        x
    }
  }
}
