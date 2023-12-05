package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Template
import scala.meta.Tree
import scala.meta.Type
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

object NestedClassTrait {
  private object ClassOrTrait {
    def unapply(t: Tree): Option[(Type.Name, Template)] = PartialFunction.condOpt(t) {
      case x: Defn.Trait if !x.mods.exists(_.is[Mod.Implicit]) =>
        (x.name, x.templ)
      case x: Defn.Class if !x.mods.exists(_.is[Mod.Implicit]) =>
        (x.name, x.templ)
    }
  }
}

class NestedClassTrait extends SyntacticRule("NestedClassTrait") {
  import NestedClassTrait.ClassOrTrait
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case ClassOrTrait(_, template) =>
      template.stats.collect { case ClassOrTrait(typeName, _) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "don't define nested class or trait",
            position = typeName.pos,
            severity = LintSeverity.Warning
          )
        )
      }.asPatch
    }.asPatch
  }
}
