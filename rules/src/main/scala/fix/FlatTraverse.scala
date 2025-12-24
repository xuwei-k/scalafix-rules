package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class FlatTraverse extends SyntacticRule("FlatTraverse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            Term.Select(
              Term.Apply.After_4_6_0(
                Term.Select(
                  qual,
                  Term.Name("traverse")
                ),
                a @ Term.ArgClause(
                  arg :: Nil,
                  None
                )
              ),
              Term.Name("map")
            ),
            Term.ArgClause(
              Term.AnonymousFunction(
                Term.Select(
                  Term.Placeholder(),
                  Term.Name("flatten")
                )
              ) :: Nil,
              None
            )
          ) =>
        if (a.tokens.dropWhile(_.is[Token.Whitespace]).headOption.exists(_.is[Token.LeftBrace])) {
          Patch.replaceTree(t, s"${qual}.flatTraverse${arg}")
        } else {
          Patch.replaceTree(t, s"${qual}.flatTraverse(${arg})")
        }
    }
  }.asPatch
}
