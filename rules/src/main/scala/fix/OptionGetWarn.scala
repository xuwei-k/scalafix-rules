package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.ByNameType
import scalafix.v1.Configuration
import scalafix.v1.MethodSignature
import scalafix.v1.Rule
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

final case class OptionGetWarnConfig(
  message: String
)

object OptionGetWarnConfig {
  val default: OptionGetWarnConfig = OptionGetWarnConfig(
    message = "Don't use Option.get"
  )

  implicit val surface: Surface[OptionGetWarnConfig] =
    metaconfig.generic.deriveSurface[OptionGetWarnConfig]

  implicit val decoder: ConfDecoder[OptionGetWarnConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class OptionGetWarn(config: OptionGetWarnConfig) extends SemanticRule("OptionGetWarn") {

  def this() = this(OptionGetWarnConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("OptionGetWarn")(this.config).map(newConfig => new OptionGetWarn(newConfig))
  }
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect { case Term.Select(obj, get @ Term.Name("get")) =>
      def p = Patch.lint(
        Diagnostic(
          id = "",
          message = config.message,
          position = get.pos,
          severity = LintSeverity.Warning
        )
      )
      obj.symbol.info.flatMap { i =>
        PartialFunction.condOpt(i.signature) {
          case ValueSignature(t: TypeRef) if t.symbol.normalized.value == "scala.Option." =>
            p
          case MethodSignature(_, _, t: TypeRef) if t.symbol.normalized.value == "scala.Option." =>
            p
          case ValueSignature(ByNameType(t: TypeRef)) if t.symbol.normalized.value == "scala.Option." =>
            p
        }
      }.asPatch
    }.asPatch
  }
}
