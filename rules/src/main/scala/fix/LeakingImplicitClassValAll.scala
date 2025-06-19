package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Name
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.XtensionSeqPatch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class LeakingImplicitClassValAll extends SyntacticRule("LeakingImplicitClassValAll") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class if t.mods.exists(_.is[Mod.Implicit]) =>
        PartialFunction
          .condOpt(t.ctor.paramClauses.map(_.values.map(_.mods))) {
            case Seq(Seq(mods)) if mods.exists(_.is[Mod.ValParam]) && mods.collectFirst {
                  case m: Mod.Private if m.within.is[Name.Anonymous] || m.within.is[Term.This] => ()
                }.isEmpty =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = "make private implicit class underlying value",
                  position = t.name.pos,
                  severity = LintSeverity.Warning
                )
              )
          }
          .asPatch
    }.asPatch
  }
}
