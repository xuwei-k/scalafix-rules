package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class GroupMap extends SyntacticRule("GroupMap") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.Initial(
              Term.Select(
                Term.Select(
                  Term.Apply.Initial(
                    Term.Select(
                      obj,
                      Term.Name("groupBy")
                    ),
                    f1 :: Nil
                  ),
                  Term.Name("view")
                ),
                Term.Name("mapValues")
              ),
              List(
                Term.AnonymousFunction(
                  Term.Apply.Initial(
                    Term.Select(
                      Term.Placeholder(),
                      Term.Name("map")
                    ),
                    f2 :: Nil
                  )
                )
              )
            ),
            Term.Name("toMap")
          ) =>
        Patch.replaceTree(t, s"${obj}.groupMap($f1)($f2)")
    }.asPatch
  }
}
