package fix

import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionTreeScalafix
import scalafix.v1.XtensionSeqPatch
import scala.meta.Enumerator
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI

class FutureForIf extends SemanticRule("FutureForIf") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect { case t: Term.ForClause =>
      if (
        t.enumsBlock.enums.exists {
          case g: Enumerator.Generator =>
            g.rhs.symbol.info.exists { i =>
              PartialFunction.cond(i.signature) {
                case ValueSignature(TypeRef(_, sym, _ :: Nil)) if sym.value == "scala/concurrent/Future#" =>
                  true
              }
            }
          case _ =>
            false
        }
      ) {
        t.enumsBlock.enums.collect { case i: Enumerator.Guard =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "Don't use Future.withFilter",
              position = i.pos,
              severity = LintSeverity.Warning
            )
          )
        }.asPatch
      } else {
        Patch.empty
      }
    }.asPatch
  }
}
