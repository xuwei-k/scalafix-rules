package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term

class OptionFilter extends SyntacticRule("OptionFilter") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match(
            expr,
            List(
              Case(
                Pat.Extract(Term.Name("Some"), Pat.Var(Term.Name(a1)) :: Nil),
                Some(condition),
                Term.Apply(Term.Name("Some"), Term.Name(a2) :: Nil)
              ),
              Case(
                Pat.Wildcard(),
                None,
                Term.Name("None")
              )
            )
          ) if a1 == a2 =>
        Patch.replaceTree(t, s"${expr}.filter(${a1} => ${condition})")
    }.asPatch
  }
}
