package fix

import scala.meta.Case
import scala.meta.Lit
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Term.Block
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

/**
 * [[https://github.com/scala/scala/blob/v2.13.12/src/library/scala/Option.scala#L396-L402]]
 * [[https://github.com/scala/scala/blob/v2.13.12/src/library/scala/Option.scala#L411-L417]]
 */
class OptionForallExists extends SyntacticRule("OptionForallExists") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(
            expr,
            List(
              Case(
                Pat.Extract.After_4_6_0(Term.Name("Some"), Pat.ArgClause(Pat.Var(Term.Name(a1)) :: Nil)),
                None,
                predicate
              ),
              Case(
                Pat.Wildcard() | Term.Name("None"),
                None,
                Lit.Boolean(bool)
              )
            ),
            _
          ) =>
        val method = if (bool) "forall" else "exists"
        val (open, close) = {
          predicate match {
            case Block(stats) if stats.size > 1 && !predicate.tokens.forall(_.is[Token.LeftBrace]) =>
              "{" -> "}"
            case _ =>
              "(" -> ")"
          }
        }
        Patch.replaceTree(t, s"${expr}.${method}${open}${a1} => ${predicate}${close}")
    }.asPatch
  }
}
