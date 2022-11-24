package fix

import scala.meta._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class CaseClassPrivateConstructor extends SyntacticRule("CaseClassPrivateConstructor") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case c: Defn.Class
          if c.ctor.mods.exists(_.is[Mod.Private]) && !c.mods
            .exists(_.is[Mod.Abstract]) && c.mods.exists(_.is[Mod.Case]) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "case class private constructor",
            position = c.ctor.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
