package fix

import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import metaconfig.Configured

class DiscardScalaFuture(config: DiscardSingleConfig) extends SemanticRule("DiscardScalaFuture") {

  def this() = this(DiscardSingleConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("DiscardScalaFuture")(this.config).map(newConfig => new DiscardScalaFuture(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch =
    DiscardValue.typeRef(config.toDiscardValueConfig("scala/concurrent/Future#"))
}
