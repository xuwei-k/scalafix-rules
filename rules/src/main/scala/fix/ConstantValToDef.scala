package fix

import scala.meta.Defn
import scala.meta.Lit
import scala.meta.Mod
import scala.meta.Name
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class ConstantValToDef extends SyntacticRule("ConstantValToDef") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    Patch.fromIterable(
      doc.tree.collect {
        case t: Defn.Class =>
          t.templ
        case t: Defn.Trait =>
          t.templ
      }.flatMap(
        _.body.stats.collect {
          case v @ Defn.Val(
                _,
                Pat.Var(_: Term.Name) :: Nil,
                _,
                _: Lit
              ) if !v.mods.forall(m => m.is[Mod.Inline] || m.is[Mod.Final]) && v.mods.exists {
                case Mod.Private(Name.Anonymous()) => true
                case Mod.Private(Term.This(Name.Anonymous())) => true
                case _ => false
              } =>
            v.tokens.find(_.is[Token.KwVal]).map(Patch.replaceToken(_, "def")).asPatch
        }
      )
    )
  }
}
