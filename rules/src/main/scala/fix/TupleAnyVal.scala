package fix

import scalafix.v1._
import scala.meta._

class TupleAnyVal extends SyntacticRule("TupleAnyVal") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Type.Tuple if t.args.sizeIs > 2 =>
        t.args.collect { case Type.Name("Int" | "Long" | "Boolean" | "Byte" | "Float" | "Double" | "Char" | "Short") =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "",
              position = t.pos,
            )
          )
        }.asPatch
    }.asPatch
  }
}
