package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

private object BigDecimalFromDoubleLiteral {
  private object Fun {
    def unapply(x: Term): Option[String] = PartialFunction.condOpt(x) {
      case Term.Name("BigDecimal") | Term.Select(
            Term.Name("BigDecimal"),
            Term.Name("apply")
          ) =>
        "apply"
      case Term.Select(
            Term.Name("BigDecimal"),
            Term.Name("decimal")
          ) =>
        "decimal"
    }
  }
}

class BigDecimalFromDoubleLiteral extends SyntacticRule("BigDecimalFromDoubleLiteral") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            BigDecimalFromDoubleLiteral.Fun(fun),
            Term.ArgClause(
              List(
                _: Lit.Double
              ) | List(
                _: Lit.Double,
                _
              ),
              None
            )
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = s"Don't use BigDecimal.${fun}(scala.Double literal). use String literal",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
