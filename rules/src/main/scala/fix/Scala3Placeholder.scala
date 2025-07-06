package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class Scala3PlaceholderConfig(
  message: String
)

object Scala3PlaceholderConfig {
  val default: Scala3PlaceholderConfig = Scala3PlaceholderConfig(
    message = "use ? instead of _"
  )

  implicit val surface: Surface[Scala3PlaceholderConfig] =
    metaconfig.generic.deriveSurface[Scala3PlaceholderConfig]

  implicit val decoder: ConfDecoder[Scala3PlaceholderConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class Scala3Placeholder(config: Scala3PlaceholderConfig) extends SyntacticRule("Scala3Placeholder") {

  def this() = this(Scala3PlaceholderConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("Scala3Placeholder")(this.config).map(newConfig => new Scala3Placeholder(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Type.Wildcard(bounds)
          if bounds.context.isEmpty && bounds.lo.isEmpty && bounds.hi.isEmpty && (t.toString == "_") =>
        Seq(
          Patch.lint(
            Diagnostic(
              id = "",
              message = config.message,
              position = t.pos,
              severity = LintSeverity.Warning
            )
          ),
          Patch.replaceTree(t, "?")
        ).asPatch
    }.asPatch
  }
}
