package fix

import scala.meta.Import
import scala.meta.Importer
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class CatsImplicitsImport extends SyntacticRule("CatsImplicitsImport") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Import(
            Importer(Term.Select(Term.Name("cats"), Term.Name("implicits")), _) :: Nil
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "use `cats.syntax` instead of `cats.implicits` https://github.com/typelevel/cats/issues/4138",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }
  }.asPatch
}
