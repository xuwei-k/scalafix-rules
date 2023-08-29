package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class FlatMapCollect extends SyntacticRule("FlatMapCollect") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(_, flatMap @ Term.Name("flatMap")),
            Term.ArgClause(
              List(
                pf @ Term.PartialFunction(
                  cases :+ (
                    noneCase @ Case(Pat.Wildcard() | Pat.Var(_: Term.Name), None, Term.Name("None"))
                  )
                )
              ),
              None
            )
          ) =>
        val someCase = cases.collect {
          case Case(
                _,
                _,
                x @ Term.Apply.After_4_6_0(
                  s @ Term.Name("Some"),
                  _
                )
              ) =>
            Seq(
              Patch.removeTokens(s.tokens),
              x.tokens.reverseIterator.find(_.is[Token.RightParen]).map(Patch.removeToken).asPatch,
              x.tokens.find(_.is[Token.LeftParen]).map(Patch.removeToken).asPatch
            ).asPatch
        }
        if (someCase.lengthCompare(cases.size) == 0) {
          Seq(
            Patch.replaceTree(flatMap, "collect"),
            Patch.removeTokens(noneCase.tokens),
            pf.tokens.reverseIterator
              .find(t => t.is[Token.LF] && t.pos.start < noneCase.pos.start)
              .map { t =>
                Seq(
                  Patch.removeToken(t),
                  Patch.removeTokens(
                    pf.tokens.filter(x =>
                      (t.start < x.pos.start) && (x.pos.start < noneCase.pos.start) && t.is[Token.Whitespace]
                    )
                  )
                ).asPatch
              }
              .asPatch,
            someCase.asPatch
          ).asPatch
        } else {
          Patch.empty
        }
    }.asPatch
  }
}
