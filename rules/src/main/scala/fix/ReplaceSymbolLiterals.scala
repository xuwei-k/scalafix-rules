package fix

import scalafix.v1.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Lit

class ReplaceSymbolLiterals extends SyntacticRule("ReplaceSymbolLiterals") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case literal @ Lit.Symbol(_) =>
      Patch.replaceTree(literal, s"""Symbol("${literal.value.name}")""")
    }.foldLeft(Patch.empty)(_ + _)
  }

}
