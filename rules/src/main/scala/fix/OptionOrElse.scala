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

class OptionOrElse extends SyntacticRule("OptionOrElse") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(
            expr,
            List(
              Case(
                Pat.Extract.Initial(Term.Name("Some"), Pat.Var(Term.Name(a1)) :: Nil),
                None,
                Term.Apply.Initial(Term.Name("Some"), Term.Name(a2) :: Nil)
              ),
              Case(
                Pat.Wildcard() | Term.Name("None"),
                None,
                alternative
              )
            ),
            _
          ) if (a1 == a2) && alternative.collectFirst { case _: Term.Return => () }.isEmpty =>
        val (open, close) = {
          alternative match {
            case Block(stats) if stats.size > 1 && !alternative.tokens.forall(_.is[Token.LeftBrace]) =>
              "{" -> "}"
            case _ =>
              "(" -> ")"
          }
        }
        Patch.replaceTree(t, s"${expr}.orElse${open}${alternative}${close}")
    }.asPatch
  }
}
