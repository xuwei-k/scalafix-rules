package fix

import scala.meta._
import scalafix.v1._

class ImplicitValGiven extends SyntacticRule("ImplicitValGiven") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Val(
            _,
            List(
              Pat.Var(_: Term.Name)
            ),
            Some(
              _
            ),
            _
          ) if t.mods.exists(_.is[Mod.Implicit]) =>
        Seq(
          t.tokens.find(_.is[scala.meta.tokens.Token.KwVal]).map(Patch.replaceToken(_, "given")),
          t.tokens.zip(t.tokens.drop(1)).find(_._1.is[scala.meta.tokens.Token.KwLazy]).map { case (t1, t2) =>
            Patch.removeTokens(t1 :: t2 :: Nil)
          },
          t.tokens.zip(t.tokens.drop(1)).find(_._1.is[scala.meta.tokens.Token.KwImplicit]).map { case (t1, t2) =>
            Patch.removeTokens(t1 :: t2 :: Nil)
          }
        ).flatten.asPatch
    }.asPatch
  }
}
