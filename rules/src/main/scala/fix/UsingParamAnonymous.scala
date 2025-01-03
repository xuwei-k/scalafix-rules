package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UsingParamAnonymous extends SyntacticRule("UsingParamAnonymous") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Defn.Def =>
      t.paramClauses.filter { paramClause =>
        paramClause.values.forall(
          _.mods.exists(_.is[Mod.Using])
        ) && paramClause.values.forall(
          !_.mods.exists(_.is[Mod.Inline])
        )
      }.map { paramClause =>
        val countOneNames = t.collect { case x: Term.Name =>
          x.value
        }.groupBy(identity).filter(_._2.size == 1).keySet
        if (paramClause.values.forall(a => countOneNames(a.name.value))) {
          paramClause.values
            .map(p =>
              Seq(
                Patch.removeTokens(p.name.tokens),
                Patch.removeToken(p.tokens.find(_.is[Token.Colon]).get)
              ).asPatch
            )
            .asPatch
        } else {
          Patch.empty
        }
      }.asPatch
    }.asPatch
  }
}
