package fix

import scala.meta._
import scala.meta.tokens.Token
import scalafix.v1._

object ScalaUtilUsingResource {
  private object SingleBlockOr {
    def unapply(value: Term): Option[Stat] = PartialFunction.condOpt(value) {
      case Term.Block(x :: Nil) => x
      case x => x
    }
  }

  private object Close {
    def unapply(value: Term): Option[Term.Name] = PartialFunction.condOpt(value) {
      case Term.Apply.After_4_6_0(
            Term.Select(
              x: Term.Name,
              Term.Name("close")
            ),
            Term.ArgClause(Nil, None)
          ) =>
        x
      case Term.Select(
            x: Term.Name,
            Term.Name("close")
          ) =>
        x
    }
  }
}

class ScalaUtilUsingResource extends SyntacticRule("ScalaUtilUsingResource") {
  override def fix(implicit doc: SyntacticDocument): Patch =
    fix0(doc.tree).asPatch

  private def fix0(tree: Tree)(implicit doc: SyntacticDocument): Seq[Patch] =
    tree.collect { case t: Term.Block =>
      t.stats.zip(t.stats.drop(1)).collect {
        case (
              a1 @ Defn.Val(
                Nil,
                List(
                  Pat.Var(x1: Term.Name)
                ),
                _,
                _
              ),
              a2 @ Term.Try.After_4_9_9(
                _,
                None,
                Some(
                  finallyTerm @ ScalaUtilUsingResource.SingleBlockOr(
                    ScalaUtilUsingResource.Close(x2)
                  )
                )
              )
            ) if (x1.value == x2.value) && fix0(a2.expr).isEmpty =>
          val braceOption = a2.tokens
            .find(_.is[Token.KwTry])
            .zip(
              a2.tokens.find(_.is[Token.LeftBrace])
            )
            .collect {
              case (tryToken, braceToken)
                  if a2.tokens
                    .filter(x => (tryToken.pos.start < x.pos.start) && (x.pos.start < braceToken.start))
                    .forall(_.is[Token.HTrivia]) =>
                braceToken
            }
          Seq(
            Patch.removeTokens(a1.tokens),
            if (a1.pos.endLine < a2.pos.startLine) {
              t.tokens.filter(_.is[Token.LF]).find(_.pos.start > a1.pos.end).map(Patch.removeToken).asPatch
            } else {
              Patch.empty
            },
            Patch.replaceToken(
              a2.tokens.find(_.is[Token.KwTry]).get,
              s"Using.resource(${a1.rhs}) { ${x1} =>\n"
            ),
            Patch.removeTokens(braceOption),
            Patch.replaceToken(
              a2.tokens.reverseIterator.find(_.is[Token.KwFinally]).get,
              if (braceOption.isEmpty) {
                "}"
              } else {
                ""
              }
            ),
            Patch.removeTokens(finallyTerm.tokens),
            Patch.addGlobalImport(importer"scala.util.Using")
          )
      }
    }.flatten.flatten
}
