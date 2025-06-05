package fix

import scala.meta.Mod
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ImplicitImplicit extends SyntacticRule("ImplicitImplicit") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Term.ParamClause if t.mod.exists(_.is[Mod.Implicit]) =>
        val tokens = t.values.drop(1).flatMap(_.tokens)

        tokens
          .zip(tokens.drop(1))
          .collect { case (i: Token.KwImplicit, next) =>
            Seq(
              if (next.is[Token.Space]) {
                Patch.removeToken(next)
              } else {
                Patch.empty
              },
              Patch.removeToken(i)
            ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
