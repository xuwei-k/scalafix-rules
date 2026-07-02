package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

class UseGivenByExplicitName extends SemanticRule("UseGivenByExplicitName") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t: Term.Select if t.name.symbol.info.exists(_.isGiven) =>
        Seq(t.name)
      case i: Import =>
        i.importers.flatMap(_.importees).collect {
          case x: Importee.Name if x.name.symbol.info.exists(_.isGiven) =>
            x
          case x: Importee.Rename if x.name.symbol.info.exists(_.isGiven) =>
            x
        }
    }.flatten.map { t =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = "Don't use given by name",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    }.asPatch
  }
}
