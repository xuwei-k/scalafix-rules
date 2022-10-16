package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term
import scala.meta.Term.ApplyUnary

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
