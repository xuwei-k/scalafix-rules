package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UnnecessaryMatch extends SyntacticRule("UnnecessaryMatch") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case f @ Term.Function.After_4_6_0(
            Term.ParamClause(Term.Param(_, Term.Name(p1), _, _) :: Nil, _),
            Term.Match.After_4_4_5(Term.Name(p2), cases, _)
          ) if p1 == p2 && cases.forall(_.collect { case Term.Name(n) if n == p2 => () }.isEmpty) =>
        Seq(
          Patch.removeTokens(f.tokens.takeWhile(!_.is[Token.KwMatch])),
          Patch.removeTokens(f.tokens.find(_.is[Token.KwMatch]))
        ).asPatch
    }
  }.asPatch
}
