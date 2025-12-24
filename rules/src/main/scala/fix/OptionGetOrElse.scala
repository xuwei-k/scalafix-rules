package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Term.Block
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.contrib.XtensionTreeOps
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

/**
 * [[https://github.com/scala/scala/blob/v2.13.12/src/library/scala/Option.scala#L190-L196]]
 */
class OptionGetOrElse extends SyntacticRule("OptionGetOrElse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(
            expr,
            List(
              Case(
                Pat.Extract.After_4_6_0(Term.Name("Some"), Pat.ArgClause(Pat.Var(Term.Name(a1)) :: Nil)),
                None,
                Term.Name(a2)
              ),
              Case(
                Pat.Wildcard() | Term.Name("None"),
                None,
                default
              )
            ),
            _
          ) if a1 == a2 && default.collectFirst { case _: Term.Return => () }.isEmpty =>
        val (open, close) = {
          default match {
            case Block(stats) if stats.size > 1 && !default.tokens.forall(_.is[Token.LeftBrace]) =>
              "{" -> "}"
            case _ =>
              "(" -> ")"
          }
        }
        Patch.replaceTree(t, s"${expr}.getOrElse${open}${default}${close}")
    }.asPatch
  }
}
