package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token.LeftBrace
import scala.meta.tokens.Token.RightBrace
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class MapSequenceTraverse extends SyntacticRule("MapSequenceTraverse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(
                qual,
                Term.Name("map")
              ),
              Term.ArgClause(
                arg :: Nil,
                None
              )
            ),
            Term.Name("sequence")
          ) =>
        (arg.tokens.headOption, arg.tokens.lastOption) match {
          case (Some(LeftBrace()), Some(RightBrace())) => Patch.replaceTree(t, s"${qual}.traverse${arg}")
          case _ => Patch.replaceTree(t, s"${qual}.traverse(${arg})")
        }
    }
  }.asPatch
}
