package fix

import scala.meta.*
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object ScalaBug4940 {
  object F {
    def unapply(x: Term): Boolean = PartialFunction.cond(x) {
      case Term.AnonymousFunction(
            Term.Match.After_4_4_5(
              Term.Placeholder(),
              _,
              Nil
            )
          ) =>
        true

      case Term.Function.After_4_6_0(
            Term.ParamClause(
              Term.Param(Nil, x1: Term.Name, _, _) :: Nil,
              _
            ),
            Term.Match.After_4_4_5(
              x2: Term.Name,
              _,
              _
            )
          ) if x1.value == x2.value =>
        true
    }
  }
}

class ScalaBug4940 extends SyntacticRule("ScalaBug4940") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            Term.Select(_, Term.Name("collect" | "collectFirst")),
            Term.ArgClause(
              Term.Block(
                List(
                  ScalaBug4940.F()
                )
              ) :: Nil,
              _
            )
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "https://github.com/scala/bug/issues/4940",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
