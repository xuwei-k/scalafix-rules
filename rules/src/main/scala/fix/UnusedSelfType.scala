package fix

import scala.meta.Self
import scala.meta.Template
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scala.meta.contrib.XtensionTreeOps
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UnusedSelfType extends SyntacticRule("UnusedSelfType") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Template.After_4_9_9(
            _,
            _,
            Template.Body(
              Some(Self(a, None)),
              _
            ),
            _
          )
          if t.body.stats.forall(s =>
            s.collectFirst {
              case x: Term.Name if x.value == a.value => ()
            }.isEmpty
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "unused self type",
            position = a.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
