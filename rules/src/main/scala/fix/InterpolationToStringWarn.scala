package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Term
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class InterpolationToStringWarnConfig(
  message: String
)

object InterpolationToStringWarnConfig {
  val default: InterpolationToStringWarnConfig = InterpolationToStringWarnConfig(
    message = "maybe unnecessary `toString` in the `s` interpolation"
  )

  implicit val surface: Surface[InterpolationToStringWarnConfig] =
    metaconfig.generic.deriveSurface[InterpolationToStringWarnConfig]

  implicit val decoder: ConfDecoder[InterpolationToStringWarnConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class InterpolationToStringWarn(config: InterpolationToStringWarnConfig)
    extends SyntacticRule("InterpolationToStringWarn") {

  def this() = this(InterpolationToStringWarnConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("InterpolationToStringWarn")(this.config)
      .map(newConfig => new InterpolationToStringWarn(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Interpolate(
            Term.Name("s"),
            _,
            values
          ) =>
        values.collect {
          case InterpolationToStringWarn.SelectToString(t) =>
            t
          case Term.Block(InterpolationToStringWarn.SelectToString(t) :: Nil) =>
            t
        }.map { t =>
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
  }.asPatch
}

private object InterpolationToStringWarn {
  private object SelectToString {
    def unapply(t: Tree): Option[Term.Name] = PartialFunction.condOpt(t) {
      case Term.Select(
            _,
            x @ Term.Name("toString")
          ) =>
        x
      case Term.Apply.After_4_6_0(
            Term.Select(_, x @ Term.Name("toString")),
            Term.ArgClause(Nil, None)
          ) =>
        x
    }
  }
}
