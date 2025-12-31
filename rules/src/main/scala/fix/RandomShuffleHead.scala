package fix

import scala.meta.Term
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

class RandomShuffleHead extends SemanticRule("RandomShuffleHead") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(
                random,
                Term.Name("shuffle")
              ),
              Term.ArgClause(
                _ :: Nil,
                None
              )
            ),
            Term.Name("head" | "last")
          ) if (random.symbol.value == "scala/util/Random.") || random.symbol.info.map(_.signature).exists {
            case ValueSignature(TypeRef(_, symbol, Nil)) =>
              symbol.value == "scala/util/Random#"
            case _ =>
              false
          } =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = s"use `seq(${random}.nextInt(seq.size))` instead",
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }.asPatch
  }
}
