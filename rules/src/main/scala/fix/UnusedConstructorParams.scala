package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Stat
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.termParamClauseToValues
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UnusedConstructorParams extends SyntacticRule("UnusedConstructorParams") {
  override def isLinter = true

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val message = "maybe unused constructor param"
    doc.tree.collect {
      case x: (Stat.WithTemplate & Stat.WithCtor & Stat.WithMods)
          if (x.is[Defn.Class] || x.is[Defn.Trait]) &&
            !x.mods.exists(_.is[Mod.Case]) =>
        val params = x.ctor.paramClauses.flatten
          .filterNot(_.mods.exists(_.is[Mod.Implicit]))
          .filterNot(_.mods.exists(_.is[Mod.Using]))
          .filterNot(_.mods.exists(_.is[Mod.VarParam]))
          .filterNot(_.mods.exists(_.is[Mod.ValParam]))
        val allTokens = {
          val values = x.templ.tokens.map(_.text).toSet
          values ++ values.filter(a => a.startsWith("`") && a.endsWith("`")).map(_.drop(1).dropRight(1))
        }
        val maybeUnused = params.filterNot(p => allTokens(p.name.value))
        maybeUnused.map { a =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = message,
              position = a.pos,
              severity = LintSeverity.Warning
            )
          )
        }.asPatch
      case x: Defn.Enum =>
        val params = x.ctor.paramClauses.flatten.filterNot(
          _.mods.exists(m => m.is[Mod.Implicit] || m.is[Mod.Using] || m.is[Mod.VarParam] || m.is[Mod.ValParam])
        )
        val allTokens = {
          val values =
            x.templ.body.stats
              .filterNot(s => s.is[Defn.EnumCase] || s.is[Defn.RepeatedEnumCase])
              .flatMap(_.tokens)
              .map(_.text)
              .toSet
          values ++ values.filter(a => a.startsWith("`") && a.endsWith("`")).map(_.drop(1).dropRight(1))
        }
        val maybeUnused = params.filterNot(p => allTokens(p.name.value))
        maybeUnused.map { a =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = message,
              position = a.pos,
              severity = LintSeverity.Warning
            )
          )
        }.asPatch
    }.asPatch
  }
}
