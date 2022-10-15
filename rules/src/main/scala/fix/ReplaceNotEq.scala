package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term
import scala.meta.Term.ApplyInfix
import scala.meta.Term.ApplyUnary

class ReplaceNotEq extends SyntacticRule("ReplaceNotEq") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ ApplyUnary(Term.Name("!"), ApplyInfix(lhs, Term.Name("=="), Nil, rhs :: Nil)) =>
      Patch.replaceTree(t, s"(${lhs} != ${rhs})")
    }
  }.asPatch
}
