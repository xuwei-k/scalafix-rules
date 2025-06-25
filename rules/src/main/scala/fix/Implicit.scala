package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class ImplicitConfig(
  message: String
)

object ImplicitConfig {
  val default: ImplicitConfig = ImplicitConfig(
    message = "don't use `implicit`. use `given`, `using` or `extension`"
  )

  implicit val surface: Surface[ImplicitConfig] =
    metaconfig.generic.deriveSurface[ImplicitConfig]

  implicit val decoder: ConfDecoder[ImplicitConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class Implicit(config: ImplicitConfig) extends SyntacticRule("Implicit") {

  def this() = this(ImplicitConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("Implicit")(this.config).map(newConfig => new Implicit(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tokens.collect { case t: Token.KwImplicit =>
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
