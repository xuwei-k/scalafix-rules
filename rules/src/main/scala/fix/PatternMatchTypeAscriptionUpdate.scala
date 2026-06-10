package fix

import scala.meta._
import scala.meta.tokens.Token
import scalafix.v1._

/**
 * [[https://github.com/scala/scala3/pull/25597]]
 */
class PatternMatchTypeAscriptionUpdate extends SyntacticRule("PatternMatchTypeAscriptionUpdate") {
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
          ) =>
        Seq(
          t.tokens.collectFirst {
            case x if x.is[Token.Colon] && p.pos.end <= x.pos.start => Patch.removeToken(x)
          }.asPatch,
          Patch.removeTokens(tpe.tokens),
          p.args
            .zip(tpe.args)
            .collect { case (p, tp) =>
              Patch.addRight(p, s": ${tp}")
            }
            .asPatch
        ).asPatch
    }.asPatch
  }
}
