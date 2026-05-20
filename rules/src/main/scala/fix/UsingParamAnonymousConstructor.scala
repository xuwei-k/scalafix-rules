package fix

import scala.meta.Mod
import scala.meta.Stat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scala.meta.internal.Scaladoc
import scala.meta.internal.Scaladoc.TagType
import scala.meta.internal.parsers.ScaladocParser
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UsingParamAnonymousConstructor extends SyntacticRule("UsingParamAnonymousConstructor") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case c: Stat.WithCtor =>
      val scaladocParamNames: Set[String] = c.begComment.toList
        .flatMap(_.values)
        .flatMap { x =>
          ScaladocParser
            .parse(x.syntax)
            .toSeq
            .flatMap(_.para.flatMap(_.terms))
            .collect { case c @ Scaladoc.Tag(TagType.Param, _, _) =>
              c.label.map(_.value.trim)
            }
            .flatten
        }
        .toSet
      val t = c.ctor
      t.paramClauses.filter { paramClause =>
        paramClause.values.forall(_.mods.exists(_.is[Mod.Using])) && paramClause.values.forall(m =>
          !m.mods.exists(x => x.is[Mod.Inline] || x.is[Mod.Annot] || x.is[Mod.ValParam] || x.is[Mod.VarParam])
        ) && paramClause.values.forall(_.default.isEmpty)
      }.map { paramClause =>
        val countOneNames = c.collect { case x: Term.Name =>
          x.value
        }.groupBy(identity).filter(_._2.size == 1).keySet
        if (paramClause.values.forall(a => countOneNames(a.name.value) && !scaladocParamNames(a.name.value))) {
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
