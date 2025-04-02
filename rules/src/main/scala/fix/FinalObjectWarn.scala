package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.RuleName
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

class FinalObjectWarn extends SyntacticRule("FinalObjectWarn") {
  protected def severity: LintSeverity = LintSeverity.Warning

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
              severity = severity
            )
          )
        }
        .asPatch
    }.asPatch
  }
}

class FinalObjectError extends FinalObjectWarn {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
