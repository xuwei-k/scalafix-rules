package fix

import scala.meta.Decl
import scala.meta.Defn
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class DefImplicitParamToUsingParam extends SyntacticRule("DefImplicitParamToUsingParam") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Def =>
        t.paramClauses
      case t: Decl.Def =>
        t.paramClauses
    }.flatten
      .map(
        _.tokens.collect { case i: Token.KwImplicit =>
          Patch.replaceToken(i, "using")
        }.asPatch
      )
      .asPatch
  }
}
