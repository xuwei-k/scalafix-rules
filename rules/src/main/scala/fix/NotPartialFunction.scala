package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class NotPartialFunction extends SyntacticRule("NotPartialFunction") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply(
            Term.Select(_, Term.Name("collect" | "collectFirst")),
            List(
              Term.PartialFunction(
                _ :+ caseLast
              )
            )
          ) =>
        PartialFunction
          .condOpt(caseLast) {
            case c @ Case(Pat.Wildcard(), None, _) =>
              c
            case c @ Case(Pat.Var(Term.Name(x)), None, _) if x.head.isLower =>
              c
          }
          .map { c =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "collectやcollectFirstに渡してるのがPartialFunctionになっていないのでは？？？",
                position = c.pos,
                severity = LintSeverity.Error
              )
            )
          }
          .asPatch
    }.asPatch
  }
}
