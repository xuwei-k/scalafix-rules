package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

class FunctionVal extends SyntacticRule("FunctionVal") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Val(
            _,
            _,
            Some(
              _: Type.Function
            ),
            _
          ) =>
        t
      case t @ Defn.Val(
            _,
            _,
            None,
            _: Term.Function
          ) =>
        t
    }.map(t =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    ).asPatch
  }
}
