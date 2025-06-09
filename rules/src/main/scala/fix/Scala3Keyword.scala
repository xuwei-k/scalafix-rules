package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class Scala3Keyword extends SyntacticRule("Scala3Keyword") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Name("enum" | "export" | "given" | "then") if !t.tokens.exists(_.text.contains('`')) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = s"`${t.value}` is Scala 3 keyword",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
