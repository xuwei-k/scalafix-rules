package fix

import scala.meta._
import scalafix.v1._

class RemovePrivateGivenName extends SyntacticRule("RemovePrivateGivenName") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.GivenAlias.After_4_12_0(
            _,
            givenName: Term.Name,
            Nil,
            tpe,
            _
          ) if t.mods.exists {
            case Mod.Private(Name.Anonymous()) => true
            case _ => false
          } && doc.tree.collect {
            case x: Term.Name if x.value == givenName.value => ()
          }.size <= 1 =>
        Seq(
          Patch.removeTokens(givenName.tokens),
          if (givenName.pos.startLine != tpe.pos.startLine) {
            t.tokens
              .find(x => (givenName.pos.end <= x.pos.start) && (x.pos.end <= tpe.pos.start) && x.is[Token.EOL])
              .map(
                Patch.removeToken
              )
              .asPatch
          } else {
            Patch.empty
          },
          t.tokens
            .find(x => (givenName.pos.end <= x.pos.start) && (x.pos.end <= tpe.pos.start) && x.is[Token.Colon])
            .map(
              Patch.removeToken
            )
            .asPatch
        ).asPatch
    }.asPatch
  }
}
