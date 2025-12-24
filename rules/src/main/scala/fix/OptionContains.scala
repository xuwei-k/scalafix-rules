package fix

import scala.meta.Case
import scala.meta.Lit
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Term.ApplyInfix
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

/**
 * [[https://github.com/scala/scala/blob/v2.13.12/src/library/scala/Option.scala#L367-L373]]
 */
class OptionContains extends SyntacticRule("OptionContains") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(
            expr,
            List(
              Case(
                Pat.Extract.After_4_6_0(Term.Name("Some"), Pat.ArgClause(Pat.Var(Term.Name(a1)) :: Nil)),
                None,
                body
              ),
              Case(
                Pat.Wildcard() | Term.Name("None"),
                None,
                Lit.Boolean(false)
              )
            ),
            _
          ) =>
        PartialFunction
          .condOpt(body) {
            case ApplyInfix.After_4_6_0(
                  Term.Name(a2),
                  Term.Name("=="),
                  Type.ArgClause(Nil),
                  Term.ArgClause(b :: Nil, None)
                ) if a1 == a2 =>
              b
            case ApplyInfix.After_4_6_0(
                  b,
                  Term.Name("=="),
                  Type.ArgClause(Nil),
                  Term.ArgClause(Term.Name(a2) :: Nil, None)
                ) if a1 == a2 =>
              b
          }
          .map { b =>
            Patch.replaceTree(t, s"${expr}.contains(${b})")
          }
          .asPatch
    }.asPatch
  }
}
