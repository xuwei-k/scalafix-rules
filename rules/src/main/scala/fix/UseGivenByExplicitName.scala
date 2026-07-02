package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

class UseGivenByExplicitName extends SemanticRule("UseGivenByExplicitName") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    val givenDefinePositions: Set[(Int, Int)] =
      doc.tree.collect {
        case x: Defn.GivenAlias =>
          x.name
        case x: Term.Param =>
          x.name
      }.map(_.pos).map(x => (x.start, x.end)).toSet

    doc.tree.collect {
      case t: Term.Name if !givenDefinePositions((t.pos.start, t.pos.end)) && t.symbol.info.exists(_.isGiven) =>
        Seq(t)
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
