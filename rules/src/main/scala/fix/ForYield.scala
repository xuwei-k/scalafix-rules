package fix

import scala.meta.Enumerator
import scala.meta.Pat
import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class ForYieldToFlatMap extends SyntacticRule("ForYieldToFlatMap") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case f @ Term.ForYield(
            List(
              Enumerator.Generator(
                Pat.Var(Term.Name(x1)),
                rhs1
              ),
              Enumerator.Generator(
                Pat.Var(Term.Name(r1)),
                Term.Apply(
                  method,
                  List(Term.Name(x2))
                )
              )
            ),
            Term.Name(r2)
          ) if (x1 == x2) && (r1 == r2) =>
        Patch.replaceTree(f, s"$rhs1.flatMap($method)")
    }.asPatch
  }
}
