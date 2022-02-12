package fix

import metaconfig.generic.Surface
import metaconfig.ConfDecoder
import metaconfig.Configured
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.annotation.tailrec
import scala.meta.Ctor
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Pkg
import scala.meta.Template
import scala.meta.Tree
import scala.meta.inputs.Position

case class ExplicitImplicitTypesConfig(
  excludeLocal: Boolean
)

object ExplicitImplicitTypesConfig {
  val default = ExplicitImplicitTypesConfig(excludeLocal = true)

  implicit val surface: Surface[ExplicitImplicitTypesConfig] =
    metaconfig.generic.deriveSurface[ExplicitImplicitTypesConfig]

  implicit val decoder: ConfDecoder[ExplicitImplicitTypesConfig] =
    metaconfig.generic.deriveDecoder(default)
}

/**
  * @note There is similar wart in wartremover but SyntacticRule is faster
  * [[https://github.com/wartremover/wartremover/blob/317508c9e46cdabf755aec01f6b591a0d8c8acf9/core/src/main/scala/wartremover/warts/ExplicitImplicitTypes.scala#L14-L16]]
  */
class ExplicitImplicitTypes(config: ExplicitImplicitTypesConfig) extends SyntacticRule("ExplicitImplicitTypes") {
  def this() = this(ExplicitImplicitTypesConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("ExplicitImplicitTypes")(this.config).map(newConfig => new ExplicitImplicitTypes(newConfig))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t1: Defn.Val if t1.mods.exists(_.is[Mod.Implicit]) && t1.decltpe.isEmpty && isNotLocal(t1) =>
        Patch.lint(ExplicitImplicitTypesWarn(t1.pos))
      case t1: Defn.Def if t1.mods.exists(_.is[Mod.Implicit]) && t1.decltpe.isEmpty =>
        Patch.lint(ExplicitImplicitTypesWarn(t1.pos))
    }.asPatch
  }

  @tailrec
  private[this] def isNotLocal(t: Tree): Boolean = {
    if (config.excludeLocal) {
      t.parent match {
        case Some(value) =>
          value match {
            case _: Defn.Val => false
            case _: Defn.Var => false
            case _: Defn.Def => false
            case _: Ctor.Secondary => false
            case _: Template => true
            case _: Defn.Class => true
            case _: Defn.Object => true
            case _: Defn.Trait => true
            case _: Pkg.Object => true
            case _: Pkg => true
            case _ => isNotLocal(value)
          }
        case None => true
      }
    } else {
      true
    }
  }
}

case class ExplicitImplicitTypesWarn(override val position: Position) extends Diagnostic {
  override def message = "add explicit types for implicit values"
  override def severity = LintSeverity.Warning
}
