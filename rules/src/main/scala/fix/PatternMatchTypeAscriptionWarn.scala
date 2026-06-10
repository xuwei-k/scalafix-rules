package fix

import scala.meta._
import scalafix.v1._

/**
 * [[https://github.com/scala/scala3/pull/25597]]
 */
class PatternMatchTypeAscriptionWarn extends SyntacticRule("PatternMatchTypeAscriptionWarn") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Defn.Val(
            _,
            List(
              Pat.Tuple(
                _
              )
            ),
            Some(
              tpe @ Type.Tuple(
                _
              )
            ),
            _
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message =
              "Remove the type ascription or move it to a separate variable pattern https://github.com/scala/scala3/pull/25597",
            position = tpe.pos
          )
        )
    }.asPatch
  }
}
