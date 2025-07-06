package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Importee
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class Scala3ImportWarnConfig(
  message: String
)

object Scala3ImportWarnConfig {
  val default: Scala3ImportWarnConfig = Scala3ImportWarnConfig(
    message = "use `*` instead of `_` for wildcard import"
  )

  implicit val surface: Surface[Scala3ImportWarnConfig] =
    metaconfig.generic.deriveSurface[Scala3ImportWarnConfig]

  implicit val decoder: ConfDecoder[Scala3ImportWarnConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class Scala3ImportWarn(config: Scala3ImportWarnConfig) extends SyntacticRule("Scala3ImportWarn") {

  def this() = this(Scala3ImportWarnConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("Scala3ImportWarn")(this.config).map(newConfig => new Scala3ImportWarn(newConfig))
  }
  override def isLinter = true
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Importee.Wildcard if t.toString == "_" =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = config.message,
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
