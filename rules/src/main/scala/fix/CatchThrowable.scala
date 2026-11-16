package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

/**
 * [[https://github.com/scala/scala3/pull/26794]]
 */
class CatchThrowable extends SyntacticRule("CatchThrowable") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Term.Try =>
      t.cases.collect {
        case Case(
              p @ Pat.Typed(
                _,
                Type.Name("Throwable")
              ),
              _,
              _
            ) =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "Catching Throwable can lead to unexpected behavior",
              position = p.pos,
              severity = LintSeverity.Warning
            )
          )
      }.asPatch
    }.asPatch
  }
}
