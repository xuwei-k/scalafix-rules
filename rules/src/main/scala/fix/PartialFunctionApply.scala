package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

class PartialFunctionApply extends SemanticRule("PartialFunctionApply") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            t,
            Term.ArgClause(
              _ :: Nil,
              None
            )
          ) =>
        t.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(TypeRef(_, symbol, _ :: _ :: Nil)) if symbol.value == "scala/PartialFunction#" =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = "",
                  position = t.pos,
                  severity = LintSeverity.Warning
                )
              )
          }
          .asPatch
    }.asPatch
  }
}
