package fix

import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.inputs.Position

class UnusedConstructorParams extends SyntacticRule("UnusedConstructorParams") {
  override def isLinter = true

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case x: Defn.Class if !x.mods.exists(_.is[Mod.Case]) =>
        val params = x.ctor.paramClauses.flatten
          .filterNot(_.mods.exists(_.is[Mod.Implicit]))
          .filterNot(_.mods.exists(_.is[Mod.VarParam]))
          .filterNot(_.mods.exists(_.is[Mod.ValParam]))
        val allTokens = {
          val values = x.templ.tokens.map(_.text).toSet
          values ++ values.filter(a => a.startsWith("`") && a.endsWith("`")).map(_.drop(1).dropRight(1))
        }
        val maybeUnused = params.filterNot(p => allTokens(p.name.value))
        maybeUnused.map { a =>
          Patch.lint(
            UnusedParamWarn(a.pos)
          )
        }.asPatch
    }.asPatch
  }
}

case class UnusedParamWarn(override val position: Position) extends Diagnostic {
  override def message: String = s"maybe unused constructor param"

  override def severity: LintSeverity = LintSeverity.Warning
}
