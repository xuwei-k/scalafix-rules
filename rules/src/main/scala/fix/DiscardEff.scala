package fix

import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import metaconfig.Configured

class DiscardEff(config: DiscardSingleConfig) extends SemanticRule("DiscardEff") {

  def this() = this(DiscardSingleConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("DiscardEff")(this.config).map(newConfig => new DiscardEff(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch =
    DiscardValue.typeRef(config.toDiscardValueConfig("org/atnos/eff/Eff#"))
}
