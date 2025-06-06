package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.patch.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

case class JavaxInjectGuiceConfig(
  message: String
)

object JavaxInjectGuiceConfig {
  val default: JavaxInjectGuiceConfig = JavaxInjectGuiceConfig(
    message = "use com.google.inject"
  )

  implicit val surface: Surface[JavaxInjectGuiceConfig] =
    metaconfig.generic.deriveSurface[JavaxInjectGuiceConfig]

  implicit val decoder: ConfDecoder[JavaxInjectGuiceConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class JavaxInjectGuice(config: JavaxInjectGuiceConfig) extends SyntacticRule("JavaxInjectGuice") {
  def this() = this(JavaxInjectGuiceConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("JavaxInjectGuice")(this.config).map(newConfig => new JavaxInjectGuice(newConfig))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Term.Select(Term.Name("javax"), Term.Name("inject")) =>
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
