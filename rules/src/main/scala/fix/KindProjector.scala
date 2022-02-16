package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Type

case class KindProjectorConfig(
  rewriteInfixTypes: Set[String]
)

object KindProjectorConfig {
  val default: KindProjectorConfig = KindProjectorConfig(
    rewriteInfixTypes = Set.empty
  )

  implicit val surface: Surface[KindProjectorConfig] =
    metaconfig.generic.deriveSurface[KindProjectorConfig]

  implicit val decoder: ConfDecoder[KindProjectorConfig] =
    metaconfig.generic.deriveDecoder(default)
}

/**
  * [[https://github.com/lampepfl/dotty-feature-requests/issues/117]]
  */
class KindProjector(config: KindProjectorConfig) extends SyntacticRule("KindProjector") {
  def this() = this(KindProjectorConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("KindProjector")(this.config).map(newConfig => new KindProjector(newConfig))
  }

  private[this] object InfixOpExtractor {
    def unapply(name: Type.Name): Option[String] = {
      if (config.rewriteInfixTypes.isEmpty) {
        // rewrite all if config isEmpty
        Some(name.value)
      } else if (config.rewriteInfixTypes(name.value)) {
        Some(name.value)
      } else {
        None
      }
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ Type.ApplyInfix(left, InfixOpExtractor(opName), Type.Name("*")) =>
      Patch.replaceTree(t, s"${opName}[${left}, *]")
    }.asPatch
  }
}
