package fix

import scalafix.v1._
import scala.meta._

class ImplicitDefGiven extends SyntacticRule("ImplicitDefGiven") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.input match {
      case i: Input.VirtualFile if i.path.contains("rules/src") =>
        Patch.empty
      case _ =>
        doc.tree.collect {
          case t: Defn.Def if t.mods.exists(_.is[Mod.Implicit]) =>
            Seq(
              t.tokens.find(_.is[scala.meta.tokens.Token.KwDef]).map(Patch.replaceToken(_, "given")),
              t.tokens.zip(t.tokens.drop(1)).find(_._1.is[scala.meta.tokens.Token.KwImplicit]).map { case (t1, t2) =>
                Patch.removeTokens(t1 :: t2 :: Nil)
              }
            ).flatten.asPatch
        }.asPatch
    }
  }
}
