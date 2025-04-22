package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class CaseClassImplicitVal extends SyntacticRule("CaseClassImplicitVal") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class
          if t.mods.exists(_.is[Mod.Case]) && t.ctor.paramClauses.headOption.exists(
            _.values.exists(_.mods.exists(_.is[Mod.Implicit]))
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "`case class must have at least one leading non-implicit parameter list` error in Scala 3",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
