package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Importee

class Scala3ImportRewrite extends SyntacticRule("Scala3ImportRewrite") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Importee.Wildcard if t.toString == "_" =>
        Patch.replaceTree(t, "*")
    }.asPatch
  }
}
