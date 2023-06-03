package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class CollectHeadOption extends SyntacticRule("CollectHeadOption") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(obj, Term.Name("collect")),
              Term.ArgClause(func :: Nil, _)
            ),
            Term.Name("headOption")
          ) =>
        Patch.replaceTree(t, s"${obj}.collectFirst${func}")
    }
  }.asPatch
}
