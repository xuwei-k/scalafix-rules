package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

class ImplicitConversion extends SyntacticRule("ImplicitConversion") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Def
          if t.mods
            .exists(_.is[Mod.Implicit]) && t.paramClauseGroups.exists(!_.paramClauses.forall(_.mod.is[Mod.Implicit])) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "",
            position = t.pos,
            severity = LintSeverity.Error
          )
        )
    }.asPatch
  }
}
