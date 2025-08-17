package fix

import scala.meta.Defn
import scala.meta.Init
import scala.meta.Name
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class EnumUnnecessaryExtends extends SyntacticRule("EnumUnnecessaryExtends") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Enum if t.tparamClause.values.isEmpty && t.ctor.paramClauses.isEmpty =>
        t.templ.body.stats.collect { case c: Defn.EnumCase =>
          PartialFunction
            .condOpt(c.inits) {
              case List(
                    x @ Init.After_4_6_0(
                      typeName: Type.Name,
                      Name.Anonymous(),
                      Nil
                    )
                  ) if typeName.value == t.name.value =>
                c.tokens
                  .find(_.is[Token.KwExtends])
                  .map { e =>
                    val last = x.tokens.map(_.pos.start).max
                    val tokens = c.tokens.filter(p => (e.pos.start <= p.pos.start) && (p.pos.start <= last))
                    val s = c.tokens.filter(_.pos.start < e.pos.start).reverse.takeWhile(_.is[Token.Whitespace])
                    Patch.removeTokens(s ++ tokens)
                  }
                  .asPatch
            }
            .asPatch
        }.asPatch
    }.asPatch
  }
}
