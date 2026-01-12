package fix

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

class ArrayToString extends SemanticRule("ArrayToString") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(
            a,
            s @ Term.Name("toString")
          ) =>
        def p: Patch =
          Patch.lint(
            Diagnostic(
              id = "",
              message = "Don't use Array.toString",
              position = s.pos,
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
    }.asPatch
  }
}
