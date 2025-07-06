package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

final case class JavaURLConstructorsWarnConfig(
  message: String
)

object JavaURLConstructorsWarnConfig {
  val default: JavaURLConstructorsWarnConfig = JavaURLConstructorsWarnConfig(
    message = "https://bugs.openjdk.org/browse/JDK-8295949"
  )

  implicit val surface: Surface[JavaURLConstructorsWarnConfig] =
    metaconfig.generic.deriveSurface[JavaURLConstructorsWarnConfig]

  implicit val decoder: ConfDecoder[JavaURLConstructorsWarnConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class JavaURLConstructorsWarn(config: JavaURLConstructorsWarnConfig) extends SemanticRule("JavaURLConstructorsWarn") {

  def this() = this(JavaURLConstructorsWarnConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("JavaURLConstructorsWarn")(this.config)
      .map(newConfig => new JavaURLConstructorsWarn(newConfig))
  }
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case x: Term.New if x.init.tpe.symbol.value == "java/net/URL#" =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = config.message,
            position = x.init.tpe.pos,
            explanation = "https://github.com/openjdk/jdk/commit/4338f527aa81350e3636dcfbcd2eb17ddaad3914",
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
