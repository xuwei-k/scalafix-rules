package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Defn
import scala.meta.Term
import scala.meta.contrib.XtensionTreeOps
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.MethodSignature
import scalafix.v1.Rule
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.SemanticType
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionTreeScalafix

case class DiscardValueConfig(
  error: Seq[String],
  warning: Seq[String],
  info: Seq[String]
)

object DiscardValueConfig {

  val default: DiscardValueConfig = DiscardValueConfig(
    error = Nil,
    warning = Nil,
    info = Nil
  )

  implicit val surface: Surface[DiscardValueConfig] =
    metaconfig.generic.deriveSurface[DiscardValueConfig]

  implicit val decoder: ConfDecoder[DiscardValueConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class DiscardValue(config: DiscardValueConfig) extends SemanticRule("DiscardValue") {
  def this() = this(DiscardValueConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("DiscardValue")(this.config).map(newConfig => new DiscardValue(newConfig))
  }

  override def fix(implicit doc: SemanticDocument): Patch =
    DiscardValue.typeRef(config)
}

object DiscardValue {
  def typeRef(
    config: DiscardValueConfig
  )(implicit doc: SemanticDocument): Patch = {
    Seq(
      LintSeverity.Error -> config.error,
      LintSeverity.Warning -> config.warning,
      LintSeverity.Info -> config.info,
    ).withFilter(_._2.nonEmpty)
      .map { case (severity, types) =>
        fix(
          message = tpe => s"discard ${tpe}",
          severity = severity,
          filter = {
            case t: TypeRef =>
              types.toSet.apply(t.symbol.value)
            case _ =>
              false
          }
        )
      }
      .asPatch
  }

  def fix(
    message: SemanticType => String,
    severity: LintSeverity,
    filter: SemanticType => Boolean
  )(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect { case Term.Block(values :+ _) => // ignore last
      values.filter {
        case _: Defn.Val | _: Defn.Def | _: Term.Assign | _: Defn.Var =>
          false
        case x =>
          x.collectFirst {
            case Term.Apply.After_4_6_0(Term.Select(Term.Name("Mockito"), _), _) =>
              ()
            case Term.Apply.After_4_6_0(Term.Name("verify"), _) =>
              ()
          }.isEmpty
      }.flatMap(x => x.symbol.info.map(x -> _))
        .map { case (x, info) =>
          PartialFunction
            .condOpt(info.signature) {
              case m: MethodSignature =>
                m.returnType
              case v: ValueSignature =>
                v.tpe
            }
            .filter(filter)
            .map { tpe =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = message(tpe),
                  position = x.pos,
                  severity = severity,
                )
              )
            }
            .asPatch
        }
        .asPatch
    }.asPatch
  }
}
