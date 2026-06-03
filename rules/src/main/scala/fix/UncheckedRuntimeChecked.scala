package fix

import scala.meta._
import scala.meta.tokens.Token
import scalafix.v1._

class UncheckedRuntimeChecked extends SyntacticRule("UncheckedRuntimeChecked") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Annotate(
            x,
            (a @ Mod.Annot(
              Init.After_4_6_0(
                Type.Name("unchecked"),
                Name.Anonymous(),
                Nil
              )
            )) :: Nil
          ) =>
        Seq(
          Patch.removeTokens(a.tokens),
          Patch.removeTokens(t.tokens.find(n => n.is[Token.Colon] && x.pos.end <= n.pos.start).toSeq),
          Patch.addRight(x, ".runtimeChecked")
        ).asPatch
    }.asPatch
  }
}
