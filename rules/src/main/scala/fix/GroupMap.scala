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
            Term.Apply.After_4_6_0(
              Term.Select(
                Term.Select(
                  Term.Apply.After_4_6_0(
                    Term.Select(
                      obj,
                      Term.Name("groupBy")
                    ),
                    Term.ArgClause(
                      f1 :: Nil,
                      None
                    )
                  ),
                  Term.Name("view")
                ),
                Term.Name("mapValues")
              ),
              Term.ArgClause(
                List(
                  Term.AnonymousFunction(
                    Term.Apply.After_4_6_0(
                      Term.Select(
                        Term.Placeholder(),
                        Term.Name("map")
                      ),
                      Term.ArgClause(
                        f2 :: Nil,
                        None
                      )
                    )
                  )
                ),
                None
              )
            ),
            Term.Name("toMap")
          ) =>
        Patch.replaceTree(t, s"${obj}.groupMap($f1)($f2)")
    }.asPatch
  }
}
