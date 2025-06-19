package fix

import scala.meta.Defn
import scala.meta.Init
import scala.meta.Mod
import scala.meta.Name
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ImplicitClassNoParent extends SyntacticRule("ImplicitClassNoParent") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class if t.mods.exists(_.is[Mod.Implicit]) =>
        t.templ.inits.map {
          case Init.After_4_6_0(
                Type.Name("AnyVal") | Type.Select(
                  Term.Name("scala"),
                  Type.Name("AnyVal")
                ) | Type.Select(
                  Term.Select(
                    Term.Name("_root_"),
                    Term.Name("scala")
                  ),
                  Type.Name("AnyVal")
                ),
                Name.Anonymous(),
                Nil
              ) =>
            Patch.empty
          case x =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = s"Don't extends any class or trait if implicit class for prepare Scala 3 extension",
                position = x.pos,
                severity = LintSeverity.Warning
              )
            )
        }.asPatch
    }.asPatch
  }
}
