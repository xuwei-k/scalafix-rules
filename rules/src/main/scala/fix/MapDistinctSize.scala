package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class MapDistinctSize extends SyntacticRule("MapDistinctSize") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Select(
              Term.Apply(
                Term.Select(
                  x,
                  Term.Name("map")
                ),
                fun :: Nil
              ),
              Term.Name("distinct")
            ),
            Term.Name(s @ ("size" | "sizeIs" | "length" | "lengthIs"))
          ) =>
        Patch.replaceTree(t, s"${x}.distinctBy(${fun}).${s}")
    }
  }.asPatch
}
