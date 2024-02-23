package fix

import scala.meta.Lit
import scala.meta.XtensionCollectionLikeUI
import scalafix.v1.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class ReplaceSymbolLiterals extends SyntacticRule("ReplaceSymbolLiterals") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case literal @ Lit.Symbol(_) =>
      Patch.replaceTree(literal, s"""Symbol("${literal.value.name}")""")
    }.foldLeft(Patch.empty)(_ + _)
  }

}
