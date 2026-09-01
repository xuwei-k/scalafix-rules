package fix

import fix.MapPartitionMapIdentity.IdentityFunction
import fix.MapPartitionMapIdentity.SingleBlockOr
import scala.meta._
import scalafix.v1._

private object MapPartitionMapIdentity {
  private object SingleBlockOr {
    def unapply(value: Term): Option[Stat] = PartialFunction.condOpt(value) {
      case Term.Block(x :: Nil) => x
      case x => x
    }
  }

  private object IdentityFunction {
    def unapply(f: Term): Boolean = PartialFunction.cond(f) {
      case Term.Function.After_4_6_0(
            Term.ParamClause(
              List(
                Term.Param(
                  Nil,
                  x1: Term.Name,
                  None,
                  None
                )
              ),
              None
            ),
            x2: Term.Name
          ) =>
        x1.value == x2.value
      case Term.Name("identity") =>
        true
    }
  }
}

class MapPartitionMapIdentity extends SyntacticRule("MapPartitionMapIdentity") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            Term.Select(
              fun @ Term.Apply.After_4_6_0(
                Term.Select(
                  _,
                  map @ Term.Name("map")
                ),
                Term.ArgClause(
                  _ :: Nil,
                  None
                )
              ),
              partitionMap @ Term.Name("partitionMap")
            ),
            arg @ Term.ArgClause(
              SingleBlockOr(
                IdentityFunction(),
              ) :: Nil,
              None
            )
          ) =>
        Seq(
          Patch.replaceTree(map, "partitionMap"),
          Patch.removeTokens(partitionMap.tokens),
          Patch.removeTokens(arg.tokens),
          t.tokens.find(x => (fun.pos.end <= x.pos.start) && x.is[Token.Dot]).map(Patch.removeToken).asPatch
        ).asPatch
    }.asPatch
  }
}
