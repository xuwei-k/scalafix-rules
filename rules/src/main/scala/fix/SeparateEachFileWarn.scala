package fix

import metaconfig.Configured
import scala.meta._
import scalafix.v1._

class SeparateEachFileWarn(config: SeparateEachFileConfig) extends SyntacticRule("SeparateEachFileWarn") {

  def this() = this(SeparateEachFileConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf.getOrElse("SeparateEachFileWarn")(this.config).map(newConfig => new SeparateEachFileWarn(newConfig))

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val topLevelValues = doc.tree.collect {
      case t: (Stat.WithTemplate & Stat.WithMods & Member)
          if t.parent.forall(_.is[Pkg]) && t.templ.inits.isEmpty && !t.is[Defn.Object] =>
        t
    }

    if ((topLevelValues.lengthCompare(config.limit) >= 0) && topLevelValues.forall(_.mods.forall(!_.is[Mod.Sealed]))) {
      Patch.lint(
        Diagnostic(
          id = "",
          message = Seq(
            s"too many top level classes. please separate file. ${topLevelValues.size} ",
            topLevelValues.map(_.name.value).mkString("[", ", ", "]")
          ).mkString(""),
          position = topLevelValues.head.pos,
          severity = config.severity
        )
      )
    } else {
      Patch.empty
    }
  }

}
