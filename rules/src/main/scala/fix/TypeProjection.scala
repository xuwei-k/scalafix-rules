package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Type
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class TypeProjectionConfig(
  message: String
)

object TypeProjectionConfig {
  val default: TypeProjectionConfig = TypeProjectionConfig(
    message =
      "Don't use type projection https://docs.scala-lang.org/scala3/reference/dropped-features/type-projection.html"
  )

  implicit val surface: Surface[TypeProjectionConfig] =
    metaconfig.generic.deriveSurface[TypeProjectionConfig]

  implicit val decoder: ConfDecoder[TypeProjectionConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class TypeProjection(config: TypeProjectionConfig) extends SyntacticRule("TypeProjection") {

  def this() = this(TypeProjectionConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("TypeProjection")(this.config).map(newConfig => new TypeProjection(newConfig))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Type.Project =>
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
