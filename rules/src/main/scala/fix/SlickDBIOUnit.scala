package fix

import scala.meta.Lit
import scala.meta.Term
import scala.meta.transversers._
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class SlickDBIOUnit extends SyntacticRule("SlickDBIOUnit") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            Term.Select(
              Term.Name("DBIO"),
              Term.Name("successful")
            ),
            Term.ArgClause(
              Lit.Unit() :: Nil,
              None
            )
          ) =>
        Patch.replaceTree(t, "DBIO.unit")
    }.asPatch
  }
}
