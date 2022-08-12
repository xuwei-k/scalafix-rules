package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class CollectHead extends SyntacticRule("CollectHead") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(
            Term.Apply(
              Term.Select(_, collect @ Term.Name("collect")),
              _ :: Nil
            ),
            head @ Term.Name("head")
          ) =>
        Seq(
          Patch.replaceTree(collect, "collectFirst"),
          Patch.replaceTree(head, "get")
        ).asPatch
    }
  }.asPatch
}
