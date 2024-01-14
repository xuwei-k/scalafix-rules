package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Tree
import scala.meta.tokens.Token

class PartialFunctionCondOpt extends SyntacticRule("PartialFunctionCondOpt") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case x => fix0(x).asPatch }.asPatch
  }

  private def fix0(tree: Tree)(implicit doc: SyntacticDocument): Option[Patch] = PartialFunction
    .condOpt(tree) {
      case t @ Term.Match.After_4_4_5(expr, init :+ last, _) if init.nonEmpty =>
        last match {
          case Case(Pat.Wildcard(), None, Term.Name("None")) =>
            val values = init.collect {
              case Case(_, _, ap @ Term.Apply.After_4_6_0(Term.Name("Some"), Term.ArgClause(x :: Nil, _)))
                  if x.collect { case a =>
                    fix0(a)
                  }.flatten.isEmpty =>
                Patch.replaceTree(ap, x.toString)
            }
            if (values.lengthCompare(init.size) == 0) {
              t.tokens.find(_.is[Token.KwMatch]).map { kwMatch =>
                Seq(
                  Patch.removeTokens(t.tokens.takeWhile(_.pos.end <= kwMatch.pos.start)),
                  Patch.replaceToken(kwMatch, s"PartialFunction.condOpt($expr)"),
                  values.asPatch,
                  Patch.removeTokens(last.tokens),
                  t.tokens.reverseIterator
                    .find(x => x.is[Token.LF] && (x.pos.start < last.pos.start))
                    .map(x =>
                      Seq(
                        Patch.removeToken(x),
                        Patch.removeTokens(
                          t.tokens.filter(y =>
                            (x.start < y.pos.start) && (y.pos.start < last.pos.start) && x.is[Token.Whitespace]
                          )
                        )
                      ).asPatch
                    )
                    .asPatch,
                ).asPatch
              }
            } else {
              None
            }
          case _ =>
            None
        }
    }
    .flatten
}
