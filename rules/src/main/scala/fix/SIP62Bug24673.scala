package fix

import scala.meta.Enumerator
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.lint.Diagnostic
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class SIP62Bug24673 extends SyntacticRule("SIP62Bug24673") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Term.ForYield =>
      val lifted = t.enumsBlock.enums.lift
      PartialFunction
        .condOpt(
          (
            lifted(t.enumsBlock.enums.size - 2),
            lifted(t.enumsBlock.enums.size - 1),
            t.body
          )
        ) {
          case (
                Some(
                  Enumerator.Generator(
                    Pat.Var(Term.Name(a1)),
                    _
                  )
                ),
                Some(
                  lastVal: Enumerator.Val
                ),
                Term.Name(a2)
              ) if a1 == a2 =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "https://github.com/scala/scala3/issues/24673",
                position = lastVal.rhs.pos,
              )
            )
        }
        .asPatch
    }.asPatch
  }
}
