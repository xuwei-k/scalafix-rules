package fix

import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Lit
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI

class InterpolationTrailingWhitespace extends SyntacticRule("InterpolationTrailingWhitespace") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case i: Term.Interpolate =>
      i.parts.collect { case t @ Lit.String(s) =>
        PartialFunction
          .condOpt(s.split("\n").toSeq) {
            case init :+ last if init.exists(_.endsWith(" ")) =>
              Patch.replaceTree(
                t,
                (init.map(_.reverse.dropWhile(' ' == _).reverse) :+ last).mkString("\n")
              )
          }
          .asPatch
      }.asPatch
    }.asPatch
  }
}
