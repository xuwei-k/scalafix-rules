package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class MapSequenceTraverse extends SyntacticRule("MapSequenceTraverse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.Initial(
              Term.Select(
                qual,
                Term.Name("map")
              ),
              arg :: Nil
            ),
            Term.Name("sequence")
          ) =>
        Patch.replaceTree(t, s"${qual}.traverse(${arg})")
    }
  }.asPatch
}
