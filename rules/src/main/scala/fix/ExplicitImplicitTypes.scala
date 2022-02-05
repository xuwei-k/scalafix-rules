package fix

import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.inputs.Position

/**
  * @note There is similar wart in wartremover but SyntacticRule is faster
  * [[https://github.com/wartremover/wartremover/blob/317508c9e46cdabf755aec01f6b591a0d8c8acf9/core/src/main/scala/wartremover/warts/ExplicitImplicitTypes.scala#L14-L16]]
  */
class ExplicitImplicitTypes extends SyntacticRule("ExplicitImplicitTypes") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t1: Defn.Val if t1.mods.exists(_.is[Mod.Implicit]) && t1.decltpe.isEmpty =>
        Patch.lint(ExplicitImplicitTypesWarn(t1.pos))
      case t1: Defn.Def if t1.mods.exists(_.is[Mod.Implicit]) && t1.decltpe.isEmpty =>
        Patch.lint(ExplicitImplicitTypesWarn(t1.pos))
    }.asPatch.atomic
  }
}

case class ExplicitImplicitTypesWarn(override val position: Position) extends Diagnostic {
  override def message = "add explicit types for implicit values"
  override def severity = LintSeverity.Warning
}
