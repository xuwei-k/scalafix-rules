package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

class MapPartitionMap extends SyntacticRule("MapPartitionMap") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(
              Term.Apply.After_4_6_0(
                Term.Select(
                  _,
                  Term.Name("map")
                ),
                Term.ArgClause(
                  _ :: Nil,
                  None
                )
              ),
              t @ Term.Name("partitionMap")
            ),
            Term.ArgClause(
              _ :: Nil,
              None
            )
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
