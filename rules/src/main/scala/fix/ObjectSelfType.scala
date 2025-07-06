package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Defn
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

final case class ObjectSelfTypeConfig(
  message: String
)

object ObjectSelfTypeConfig {
  val default: ObjectSelfTypeConfig = ObjectSelfTypeConfig(
    message = "objects must not have a self type"
  )

  implicit val surface: Surface[ObjectSelfTypeConfig] =
    metaconfig.generic.deriveSurface[ObjectSelfTypeConfig]

  implicit val decoder: ConfDecoder[ObjectSelfTypeConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class ObjectSelfType(config: ObjectSelfTypeConfig) extends SyntacticRule("ObjectSelfType") {

  def this() = this(ObjectSelfTypeConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("ObjectSelfType")(this.config).map(newConfig => new ObjectSelfType(newConfig))
  }
  override def isLinter = true

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case obj: Defn.Object =>
      obj.templ.body.selfOpt.collect {
        case self if self.decltpe.isDefined =>
          Patch.lint(
            Diagnostic(
              id = "",
              position = self.pos,
              message = config.message,
              severity = LintSeverity.Warning,
            )
          )
      }.asPatch
    }.asPatch
  }
}
