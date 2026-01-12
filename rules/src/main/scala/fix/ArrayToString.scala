package fix

import fix.ArrayToString.BlockLastOrSelf
import scala.meta.Position
import scala.meta.Stat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.MethodSignature
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

private object ArrayToString {
  private object BlockLastOrSelf {
    def unapply(t: Term): Option[Stat] = PartialFunction.condOpt(t) {
      case Term.Block(_ :+ last) =>
        last
      case other =>
        other
    }
  }
}

class ArrayToString extends SemanticRule("ArrayToString") {
  private def check(a: Stat, pos: Position)(implicit doc: SemanticDocument): Patch = {
    def p: Patch =
      Patch.lint(
        Diagnostic(
          id = "",
          message = "Don't use Array.toString",
          position = pos,
          severity = LintSeverity.Warning
        )
      )

    if (a.symbol.value == "scala/Array.") {
      p
    } else {
      a.symbol.info
        .map(_.signature)
        .collect {
          case ValueSignature(tpe: TypeRef) if tpe.symbol.value == "scala/Array#" =>
            p
          case MethodSignature(_, _, tpe: TypeRef) if tpe.symbol.value == "scala/Array#" =>
            p
        }
        .asPatch
    }
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(
            BlockLastOrSelf(a),
            s @ Term.Name("toString")
          ) =>
        check(a, s.pos)
      case Term.Interpolate(
            Term.Name("s"),
            _,
            values
          ) =>
        values.collect { case BlockLastOrSelf(a) => check(a, a.pos) }.asPatch
    }.asPatch
  }
}
