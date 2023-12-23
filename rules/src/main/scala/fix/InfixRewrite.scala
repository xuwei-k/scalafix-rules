package fix

import scala.meta.Term
import scala.meta.Type
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.tokens.Token

object InfixRewrite {
  private val alphaChars: Set[Char] = (('a' to 'z') ++ ('A' to 'Z')).toSet
  private val exclude: Set[String] = Set(
    "eq",
    "ne",
    "in",
    "should",
    "to",
  )

  final class P(val obj: Term, val method: Term.Name, val arg: Term)

  object P {
    def unapply(x: Term): Option[P] = PartialFunction.condOpt(x) {
      case Term.ApplyInfix.After_4_6_0(
            obj,
            method: Term.Name,
            Type.ArgClause(Nil),
            Term.ArgClause(
              arg :: Nil,
              None
            )
          )
          if method.value.forall(alphaChars) &&
            !exclude(method.value) &&
            unapply(obj).isEmpty &&
            unapply(arg).isEmpty &&
            !obj.is[Term.ApplyInfix] &&
            !arg.is[Term.ApplyInfix] =>
        new P(obj = obj, method = method, arg = arg)
    }
  }
}

class InfixRewrite extends SyntacticRule("InfixRewrite") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ InfixRewrite.P(applyInfix) =>
      Seq(
        Patch.removeTokens(
          t.tokens.dropWhile(x => x.end <= applyInfix.obj.tokens.last.end).takeWhile(_.is[Token.Whitespace])
        ),
        Patch.removeTokens(
          t.tokens.dropWhile(x => x.end <= applyInfix.method.tokens.last.end).takeWhile(_.is[Token.Whitespace])
        ),
        Patch.addAround(applyInfix.method, ".", "("),
        Patch.addRight(applyInfix.arg, ")"),
      ).asPatch
    }.asPatch
  }
}
