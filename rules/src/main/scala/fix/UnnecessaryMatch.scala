package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class UnnecessaryMatch extends SyntacticRule("UnnecessaryMatch") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.Initial(
            _,
            List(
              f @ Term.Function.Initial(
                Term.Param(_, Term.Name(p1), _, _) :: Nil,
                Term.Match.After_4_4_5(Term.Name(p2), cases, _)
              )
            )
          ) if p1 == p2 && cases.forall(_.collect { case Term.Name(n) if n == p2 => () }.isEmpty) =>
        Patch.replaceTree(f, Term.PartialFunction(cases).toString)
      case Term.Apply.Initial(
            _,
            Term.Block(
              List(
                f @ Term.Function.Initial(
                  Term.Param(_, Term.Name(p1), _, _) :: Nil,
                  Term.Match.After_4_4_5(Term.Name(p2), cases, _)
                )
              )
            ) :: Nil
          ) if p1 == p2 && cases.forall(_.collect { case Term.Name(n) if n == p2 => () }.isEmpty) =>
        Patch.replaceTree(f, Term.PartialFunction(cases).toString)
    }
  }.asPatch
}
