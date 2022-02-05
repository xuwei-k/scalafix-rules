package fix

import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta._

class ObjectSelfType extends SyntacticRule("ObjectSelfType") {
  override def isLinter = true

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case obj: Defn.Object if obj.templ.self.decltpe.isDefined =>
        Patch.lint(
          new Diagnostic {
            override def position = obj.templ.self.pos
            override def message = "objects must not have a self type"
            override def severity = LintSeverity.Warning
          }
        )
    }.asPatch
  }
}
