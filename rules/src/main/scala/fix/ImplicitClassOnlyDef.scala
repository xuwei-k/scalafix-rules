package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ImplicitClassOnlyDef extends SyntacticRule("ImplicitClassOnlyDef") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class if t.mods.exists(_.is[Mod.Implicit]) =>
        t.templ.body.stats.collect {
          case _: Defn.Def =>
            Patch.empty
          case other =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = s"Don't define ${other.productPrefix} in implicit class for prepare Scala 3 extension",
                position = other.pos,
                severity = LintSeverity.Warning
              )
            )
        }.asPatch
    }.asPatch
  }
}
