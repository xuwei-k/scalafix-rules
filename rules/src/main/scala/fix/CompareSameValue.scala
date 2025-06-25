package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionStructure
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

private object CompareSameValue {
  private object X {
    private[this] val values = Set("==", "!=", "equals", "===", "eq", "ne")

    def unapply(t: Term): Option[(Term, Term)] = PartialFunction.condOpt(t) {
      case Term.Apply.After_4_6_0(
            Term.Select(
              a1,
              Term.Name(op)
            ),
            Term.ArgClause(a2 :: _, _)
          ) if values(op) =>
        (a1, a2)
      case Term.ApplyInfix.After_4_6_0(
            a1,
            Term.Name(op),
            _,
            Term.ArgClause(a2 :: Nil, _)
          ) if values(op) =>
        (a1, a2)
    }
  }
}

final case class CompareSameValueConfig(
  message: String
)

object CompareSameValueConfig {
  val default: CompareSameValueConfig = CompareSameValueConfig(
    message = "compare same values!?"
  )

  implicit val surface: Surface[CompareSameValueConfig] =
    metaconfig.generic.deriveSurface[CompareSameValueConfig]

  implicit val decoder: ConfDecoder[CompareSameValueConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class CompareSameValue(config: CompareSameValueConfig) extends SyntacticRule("CompareSameValue") {

  def this() = this(CompareSameValueConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("CompareSameValue")(this.config).map(newConfig => new CompareSameValue(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ CompareSameValue.X(a1, a2) if a1.structure == a2.structure && t.collect {
            case t if t.is[Term.Placeholder] => ()
          }.isEmpty =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = config.message,
            position = t.pos,
            explanation = "",
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
