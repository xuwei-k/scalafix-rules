package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class WithFilter extends SyntacticRule("WithFilter") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(
              Term.Apply.After_4_6_0(
                Term.Select(_, filter @ Term.Name("filter")),
                Term.ArgClause(_ :: Nil, _)
              ),
              Term.Name("map" | "flatMap" | "foreach")
            ),
            Term.ArgClause(_ :: Nil, _)
          ) =>
        Patch.replaceTree(filter, "withFilter")
    }.asPatch
  }
}
