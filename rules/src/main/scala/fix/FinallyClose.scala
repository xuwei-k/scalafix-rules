package fix

import scala.meta._
import scalafix.v1._

class FinallyClose extends SyntacticRule("FinallyClose") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Try.After_4_9_9(
            _,
            _,
            Some(
              finallyTerm
            )
          ) =>
        finallyTerm.collect { case c @ Term.Name("close") =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "",
              position = c.pos
            )
          )
        }.asPatch
    }.asPatch
  }
}
