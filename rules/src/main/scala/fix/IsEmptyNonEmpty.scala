package fix

import scala.meta.Term
import scala.meta.Term.ApplyUnary
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class IsEmptyNonEmpty extends SyntacticRule("IsEmptyNonEmpty") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ ApplyUnary(Term.Name("!"), Term.Select(obj, Term.Name("isEmpty"))) =>
        Patch.replaceTree(t, s"${obj}.nonEmpty")
      case t @ ApplyUnary(Term.Name("!"), Term.Select(obj, Term.Name("nonEmpty"))) =>
        Patch.replaceTree(t, s"${obj}.isEmpty")
    }.asPatch
  }
}
