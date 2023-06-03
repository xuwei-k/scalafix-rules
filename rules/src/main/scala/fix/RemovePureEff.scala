package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Enumerator
import scala.meta.Term
import scala.meta.Type
import scala.meta.Term.ApplyType

class RemovePureEff extends SyntacticRule("RemovePureEff") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case x1: Term.ForYield =>
      x1.enums
        .drop(1)
        .collect {
          case x @ Enumerator.Generator(
                _,
                ApplyType.Initial(Term.Select(rhs, Term.Name("pureEff")), Type.Name(_) :: Nil)
              ) =>
            Patch.replaceTree(x, Enumerator.Val(x.pat, rhs).toString)
        }
        .asPatch
    }.asPatch
  }
}
