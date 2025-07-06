package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Lit
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class NoElseConfig(
  message: String
)

object NoElseConfig {
  val default: NoElseConfig = NoElseConfig(
    message = "add `else`"
  )

  implicit val surface: Surface[NoElseConfig] =
    metaconfig.generic.deriveSurface[NoElseConfig]

  implicit val decoder: ConfDecoder[NoElseConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class NoElse(config: NoElseConfig) extends SyntacticRule("NoElse") {

  def this() = this(NoElseConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("NoElse")(this.config).map(newConfig => new NoElse(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.If.After_4_4_0(_, _, Lit.Unit(), _) =>
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
