package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scala.meta.contrib.XtensionTreeOps
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ReuseInstances extends SyntacticRule("ReuseInstances") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Case(
            x1,
            None,
            x2
          ) if x1.syntax == x2.syntax && x2.collectFirst { case _: Term.Apply => () }.isDefined =>
        x2 match {
          case Term.Apply.After_4_6_0(
                Term.Name("Left" | "Right"),
                Term.ArgClause(_ :: Nil, _)
              ) =>
            Patch.empty
          case _ =>
            val bindName = "x"
            Seq(
              x1.collect { case x @ Pat.Var(_: Term.Name) =>
                Patch.replaceTree(x, "_")
              }.asPatch,
              Patch.addLeft(x1, s"${bindName} @ "),
              Patch.replaceTree(x2, bindName)
            ).asPatch
        }
    }.asPatch
  }
}
