package fix

import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Defn
import scala.meta.Mod

class FinalObjectWarn extends SyntacticRule("FinalObjectWarn") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Defn.Object =>
      t.mods
        .find(_.is[Mod.Final])
        .map { finalMod =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "Modifier final is redundant",
              position = finalMod.pos,
              severity = LintSeverity.Warning
            )
          )
        }
        .asPatch
    }.asPatch
  }
}
