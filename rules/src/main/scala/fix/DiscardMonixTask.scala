package fix

import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import metaconfig.Configured

class DiscardMonixTask(config: DiscardSingleConfig) extends SemanticRule("DiscardMonixTask") {

  def this() = this(DiscardSingleConfig.default)
  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("DiscardMonixTask")(this.config).map(newConfig => new DiscardMonixTask(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch =
    DiscardValue.typeRef(config.toDiscardValueConfig("monix/eval/Task#"))

}
