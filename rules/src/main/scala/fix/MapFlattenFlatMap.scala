package fix

import scala.meta.Term
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class MapFlattenFlatMap extends SyntacticRule("MapFlattenFlatMap") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t1 @ Term.Select(
            t2 @ Term.Apply.Initial(
              Term.Select(_, map @ Term.Name("map")),
              _ :: Nil
            ),
            flatten @ Term.Name("flatten")
          ) =>
        Seq(
          Patch.replaceTree(map, "flatMap"),
          t1.tokens.reverseIterator
            .filter(_.is[Token.Dot])
            .find(x => t2.pos.end <= x.pos.start)
            .map(Patch.removeToken)
            .asPatch,
          Patch.removeTokens(flatten.tokens)
        ).asPatch
    }.asPatch
  }
}
