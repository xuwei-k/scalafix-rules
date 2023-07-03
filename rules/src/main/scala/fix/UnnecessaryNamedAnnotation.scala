package fix

import scala.meta.Ctor
import scala.meta.Mod
import scala.meta.Type
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class UnnecessaryNamedAnnotation extends SyntacticRule("UnnecessaryNamedAnnotation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case p: Ctor.Primary if p.mods.collect { case a: Mod.Annot => a.init.tpe }.collectFirst {
            case Type.Name("Inject") => ()
          }.isEmpty =>
        p.paramClauses
          .flatMap(_.values)
          .map {
            _.mods.collect { case a: Mod.Annot => a.init.tpe }.collect { case x @ Type.Name("Named") =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = "maybe unnecessary `@Named` annotation",
                  position = x.pos,
                  severity = LintSeverity.Warning
                )
              )
            }.asPatch
          }
          .asPatch
    }.asPatch
  }
}
