package fix

import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.rule.RuleName
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

class JavaURLConstructorsWarn extends SemanticRule("JavaURLConstructorsWarn") {
  protected def severity: LintSeverity = LintSeverity.Warning

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case x: Term.New if x.init.tpe.symbol.value == "java/net/URL#" =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "https://bugs.openjdk.org/browse/JDK-8295949",
            position = x.init.tpe.pos,
            explanation = "https://github.com/openjdk/jdk/commit/4338f527aa81350e3636dcfbcd2eb17ddaad3914",
            severity = severity
          )
        )
    }.asPatch
  }
}

class JavaURLConstructorsError extends JavaURLConstructorsWarn {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
