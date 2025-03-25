package fix

import scala.meta.Importer
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class CatsInstancesImport extends SyntacticRule("CatsInstancesImport") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Importer(
            Term.Select(
              Term.Select(Term.Name("cats"), Term.Name("instances")),
              _
            ),
            _
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "unnecessary import https://github.com/typelevel/cats/pull/3043",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }
  }.asPatch
}
