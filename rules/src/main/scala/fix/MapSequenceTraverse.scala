package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term
import scala.meta.tokens.Token.{LeftBrace, RightBrace}

class MapSequenceTraverse extends SyntacticRule("MapSequenceTraverse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.Initial(
              Term.Select(
                qual,
                Term.Name("map")
              ),
              arg :: Nil
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
