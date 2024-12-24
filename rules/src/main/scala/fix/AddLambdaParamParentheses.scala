package fix

import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class AddLambdaParamParentheses extends SyntacticRule("AddLambdaParamParentheses") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t1 @ Term.ParamClause(param :: Nil, _) if param.decltpe.nonEmpty && param.mods.isEmpty =>
        if (t1.tokens.find(_.is[Token.LeftParen]).exists(_.pos.start <= param.pos.start)) {
          Patch.empty
        } else {
          Seq(
            Patch.addLeft(param, "("),
            Patch.addRight(param, ")")
          ).asPatch
        }
    }.asPatch
  }
}
