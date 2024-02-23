package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.ByNameType
import scalafix.v1.MethodSignature
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

class OptionGetWarn extends SemanticRule("OptionGetWarn") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect { case Term.Select(obj, get @ Term.Name("get")) =>
      def p = Patch.lint(
        Diagnostic(
          id = "",
          message = "Don't use Option.get",
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
