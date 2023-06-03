package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class CollectHead extends SyntacticRule("CollectHead") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(_, collect @ Term.Name("collect")),
              Term.ArgClause(_ :: Nil, _)
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
