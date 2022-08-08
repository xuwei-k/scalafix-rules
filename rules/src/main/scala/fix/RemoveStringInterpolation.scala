package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class RemoveStringInterpolation extends SyntacticRule("RemoveStringInterpolation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Interpolate(Term.Name("s" | "f"), part1 :: Nil, Nil) if !t.syntax.contains("\"\"\"") =>
        Patch.replaceTree(t, "\"" + part1 + "\"")
    }.asPatch
  }
}
