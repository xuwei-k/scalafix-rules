package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.inputs.Position
import scalafix.Patch
import scalafix.RuleName
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class OptionMapFlatMap extends SyntacticRule("OptionMapFlatMap") {
  protected def severity: LintSeverity = LintSeverity.Warning

  private object CaseSome {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.Initial(Term.Name("Some"), Pat.Var(Term.Name(a1)) :: Nil),
            None,
            _
          ) =>
        true
    }
  }

  private object NoneToNone {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Term.Name("None") | Pat.Wildcard(),
            None,
            Term.Name("None")
          ) =>
        true
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(_, CaseSome() :: NoneToNone() :: Nil, _) =>
        Patch.lint(OptionMapFlatMapWarn(t.pos, severity))
      case t @ Term.Match.After_4_4_5(_, NoneToNone() :: CaseSome() :: Nil, _) =>
        Patch.lint(OptionMapFlatMapWarn(t.pos, severity))
      case t @ Term.PartialFunction(CaseSome() :: NoneToNone() :: Nil) =>
        Patch.lint(OptionMapFlatMapWarn(t.pos, severity))
      case t @ Term.PartialFunction(NoneToNone() :: CaseSome() :: Nil) =>
        Patch.lint(OptionMapFlatMapWarn(t.pos, severity))
    }.asPatch
  }
}

case class OptionMapFlatMapWarn(
  override val position: Position,
  override val severity: LintSeverity
) extends Diagnostic {
  override def message = "maybe you can use Option#map or flatMap"
}

class OptionMapFlatMapError extends OptionMapFlatMap {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
