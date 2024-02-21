package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.ByNameType
import scalafix.v1.MethodSignature
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionTreeScalafix

class HeadLastTailInit extends SemanticRule("HeadLastTailInit") {
  private val collectionTypePrefixs: Seq[String] = Seq(
    "scala.package.",
    "scala.Predef.",
    "scala.collection."
  )
  private val types: Set[String] = Set(
    "scala.Array.",
    "scala.util.Random.shuffle.C."
  )
  private def maybeScalaCollection(x: String): Boolean = {
    types(x) || collectionTypePrefixs.exists(prefix => x.startsWith(prefix))
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(
            obj,
            t @ Term.Name(
              "head" | "last" | "tail" | "init" | "min" | "max" | "maxBy" | "minBy" | "reduce" | "reduceLeft"
            )
          ) =>
        def p(tpe: String) = Patch.lint(
          Diagnostic(
            id = "",
            message = tpe,
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
        obj.symbol.info.flatMap { i =>
          PartialFunction.condOpt(i.signature) {
            case ValueSignature(t: TypeRef) if maybeScalaCollection(t.symbol.normalized.value) =>
              p(t.symbol.normalized.value)
            case MethodSignature(_, _, t: TypeRef) if maybeScalaCollection(t.symbol.normalized.value) =>
              p(t.symbol.normalized.value)
            case ValueSignature(ByNameType(t: TypeRef)) if maybeScalaCollection(t.symbol.normalized.value) =>
              p(t.symbol.normalized.value)
          }
        }.asPatch
    }.asPatch
  }
}
