package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class MapGetGetOrElse extends SyntacticRule("MapGetGetOrElse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply(
            Term.Select(
              Term.Apply(
                Term.Select(
                  map,
                  Term.Name("get")
                ),
                key :: Nil
              ),
              Term.Name("getOrElse")
            ),
            default :: Nil
          ) =>
        Patch.replaceTree(t, s"${map}.getOrElse(${key}, ${default})")
    }.asPatch
  }
}
