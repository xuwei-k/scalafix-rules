package fix

import scala.meta.Template
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class MapToForeach extends SyntacticRule("MapToForeach") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Template =>
        t.body.stats.collect {
          case Term.Apply.After_4_6_0(
                Term.Select(_, method @ Term.Name("map")),
                Term.ArgClause(
                  _ :: Nil,
                  None
                )
              ) =>
            Patch.replaceTree(method, "foreach")
        }.asPatch
      case Term.Block(xs :+ _) =>
        xs.collect {
          case Term.Apply.After_4_6_0(
                Term.Select(_, method @ Term.Name("map")),
                Term.ArgClause(
                  _ :: Nil,
                  None
                )
              ) =>
            Patch.replaceTree(method, "foreach")
        }.asPatch
    }.asPatch
  }
}
