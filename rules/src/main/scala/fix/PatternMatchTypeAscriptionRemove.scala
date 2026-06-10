package fix

import scala.meta._
import scala.meta.tokens.Token
import scalafix.v1._

/**
 * [[https://github.com/scala/scala3/pull/25597]]
 */
class PatternMatchTypeAscriptionRemove extends SyntacticRule("PatternMatchTypeAscriptionRemove") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Val(
            _,
            List(
              p @ Pat.Tuple(
                _
              )
            ),
            Some(
              tpe @ Type.Tuple(
                _
              )
            ),
            _
          ) if p.args.forall { case Pat.Var(_: Term.Name) => true; case _ => false } =>
        Seq(
          t.tokens.collectFirst {
            case x if x.is[Token.Colon] && p.pos.end <= x.pos.start => Patch.removeToken(x)
          }.asPatch,
          Patch.removeTokens(tpe.tokens),
        ).asPatch
    }.asPatch
  }
}
