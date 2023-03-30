package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionTreeScalafix

class OptionGetWarn extends SemanticRule("OptionGetWarn") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect { case Term.Select(obj, get @ Term.Name("get")) =>
      obj.symbol.info.flatMap { i =>
        PartialFunction.condOpt(i.signature) {
          case ValueSignature(t: TypeRef) if t.symbol.normalized.value == "scala.Option." =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "Don't use Option.get",
                position = get.pos,
                severity = LintSeverity.Warning
              )
            )
        }
      }.asPatch
    }.asPatch
  }
}
