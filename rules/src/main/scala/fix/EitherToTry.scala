package fix

import fix.EitherToTry.LeftFailure
import fix.EitherToTry.RightSuccess
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.tokens.Token
import scala.meta.transversers._
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object EitherToTry {
  private object RightSuccess {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.After_4_6_0(
              Term.Name("Right"),
              Pat.ArgClause(
                List(
                  Pat.Var(x1: Term.Name)
                )
              )
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Success"),
              Term.ArgClause(
                List(
                  x2: Term.Name
                ),
                None
              )
            )
          ) =>
        x1.value == x2.value
    }
  }

  private object LeftFailure {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.After_4_6_0(
              Term.Name("Left"),
              Pat.ArgClause(
                List(
                  Pat.Var(x1: Term.Name)
                )
              )
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Name("Failure"),
              Term.ArgClause(
                List(
                  x2: Term.Name
                ),
                None
              )
            )
          ) =>
        x1.value == x2.value
    }
  }
}

class EitherToTry extends SyntacticRule("EitherToTry") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Term.MatchLike if PartialFunction.cond(t.casesBlock) {
            case Term.CasesBlock(
                  List(
                    RightSuccess(),
                    LeftFailure(),
                  ) | List(
                    LeftFailure(),
                    RightSuccess(),
                  )
                ) =>
              t.mods.isEmpty
          } =>
        t.tokens
          .find(_.is[Token.KwMatch])
          .map { kwMatch =>
            val beforeMatch = t.tokens.filter(_.pos.start < kwMatch.pos.start).maxBy(_.pos.start)
            Seq(
              if (t.expr.tokens.exists(_.is[Token.Space])) {
                Patch.addAround(t.expr, "(", ")")
              } else {
                Patch.empty
              },
              if (beforeMatch.is[Token.Dot]) {
                Patch.addRight(kwMatch, "toTry")
              } else {
                Seq(
                  if (beforeMatch.is[Token.Space]) {
                    Patch.removeToken(beforeMatch)
                  } else {
                    Patch.empty
                  },
                  Patch.addRight(kwMatch, ".toTry")
                ).asPatch
              },
              Patch.removeTokens(
                t.tokens.filter(_.pos.start >= kwMatch.pos.start)
              )
            ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
