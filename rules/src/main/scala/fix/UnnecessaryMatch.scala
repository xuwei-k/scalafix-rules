package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class UnnecessaryMatch extends SyntacticRule("UnnecessaryMatch") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply(
            _,
            List(
              f @ Term.Function(
                Term.Param(_, Term.Name(p0), _, _) ::
                Term.Param(_, Term.Name(p1), _, _) ::
                Nil,
                Term.Match(
                  Term.Tuple(
                    Term.Name(x1) ::
                    Term.Name(x2) ::
                    Nil
                  ),
                  cases
                )
              )
            )
          ) if p0 == x1 && p1 == x2 && cases.forall(_.collect {
            case Term.Name(n) if n == x1 || n == x2 => ()
          }.isEmpty) =>
        Patch.replaceTree(f, Term.PartialFunction(cases).toString)
      case Term.Apply(
            _,
            Term.Block(
              List(
                f @ Term.Function(
                  Term.Param(_, Term.Name(p0), _, _) ::
                  Term.Param(_, Term.Name(p1), _, _) ::
                  Nil,
                  Term.Match(
                    Term.Tuple(
                      Term.Name(x1) ::
                      Term.Name(x2) ::
                      Nil
                    ),
                    cases
                  )
                )
              )
            ) :: Nil
          ) if p0 == x1 && p1 == x2 && cases.forall(_.collect {
            case Term.Name(n) if n == x1 || n == x2 => ()
          }.isEmpty) =>
        Patch.replaceTree(f, Term.PartialFunction(cases).toString)
    }
  }.asPatch
}
