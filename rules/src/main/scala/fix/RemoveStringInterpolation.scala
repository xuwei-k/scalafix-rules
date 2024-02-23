package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class RemoveStringInterpolation extends SyntacticRule("RemoveStringInterpolation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Interpolate(Term.Name("s" | "f"), part1 :: Nil, Nil) if !t.syntax.contains("\"\"\"") =>
        Patch.replaceTree(t, "\"" + part1 + "\"")
    }.asPatch
  }
}
