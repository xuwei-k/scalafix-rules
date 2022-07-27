package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class WithFilter extends SyntacticRule("WithFilter") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply(
            Term.Select(
              Term.Apply(Term.Select(_, filter @ Term.Name("filter")), _ :: Nil),
              Term.Name("map" | "flatMap" | "foreach")
            ),
            _ :: Nil
          ) =>
        Patch.replaceTree(filter, "withFilter")
    }.asPatch
  }
}
