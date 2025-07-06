package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Self
import scala.meta.Template
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.contrib.XtensionTreeOps
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class UnusedSelfTypeConfig(
  message: String
)

object UnusedSelfTypeConfig {
  val default: UnusedSelfTypeConfig = UnusedSelfTypeConfig(
    message = "unused self type"
  )

  implicit val surface: Surface[UnusedSelfTypeConfig] =
    metaconfig.generic.deriveSurface[UnusedSelfTypeConfig]

  implicit val decoder: ConfDecoder[UnusedSelfTypeConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class UnusedSelfType(config: UnusedSelfTypeConfig) extends SyntacticRule("UnusedSelfType") {

  def this() = this(UnusedSelfTypeConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("UnusedSelfType")(this.config).map(newConfig => new UnusedSelfType(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Template.After_4_9_9(
            _,
            _,
            Template.Body(
              Some(Self(a, None)),
              _
            ),
            _
          )
          if t.body.stats.forall(s =>
            s.collectFirst {
              case x: Term.Name if x.value == a.value => ()
            }.isEmpty
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = config.message,
            position = a.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
