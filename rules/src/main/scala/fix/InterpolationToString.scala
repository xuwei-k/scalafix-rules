package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class InterpolationToString extends SyntacticRule("InterpolationToString") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Interpolate(
            Term.Name("s"),
            _,
            values
          ) =>
        values.collect {
          case x1 @ Term.Select(
                x2,
                Term.Name("toString")
              ) =>
            Patch.replaceTree(x1, x2.toString)
        }.asPatch
    }
  }.asPatch
}
