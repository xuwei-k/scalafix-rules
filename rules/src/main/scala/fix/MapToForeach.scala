package fix

import scala.meta.Template
import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class MapToForeach extends SyntacticRule("MapToForeach") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Template =>
        t.stats.collect {
          case Term.Apply(
                Term.Select(_, method @ Term.Name("map")),
                _ :: Nil
              ) =>
            Patch.replaceTree(method, "foreach")
        }.asPatch
      case Term.Block(xs :+ _) =>
        xs.collect {
          case Term.Apply(
                Term.Select(_, method @ Term.Name("map")),
                _ :: Nil
              ) =>
            Patch.replaceTree(method, "foreach")
        }.asPatch
    }.asPatch
  }
}
