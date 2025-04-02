package fix

import scala.annotation.tailrec
import scala.meta.Import
import scala.meta.Importer
import scala.meta.Pkg
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.inputs.Position
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.rule.RuleName
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

class SyntacticOrganizeImports extends SyntacticRule("SyntacticOrganizeImports") {

  protected def severity: LintSeverity = LintSeverity.Warning

  private def collectWhile[A, B](list: List[A])(f: PartialFunction[A, B]): List[B] = {
    @tailrec
    def loop(values: List[A], acc: List[B]): List[B] = {
      values match {
        case x :: xs if f.isDefinedAt(x) =>
          loop(xs, f(x) :: acc)
        case _ =>
          acc
      }
    }
    loop(list, Nil).reverse
  }

  private def importToString(importer: Importer): String = {
    importer.pos.text
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case p: Pkg =>
      // find top-level imports
      val imports = collectWhile(p.body.stats.dropWhile(!_.is[Import])) { case i: Import => i }.sortBy(_.pos.startLine)
      val importers = imports.map(_.importers).collect { case i :: Nil => i }
      if (imports.nonEmpty && (imports.lengthCompare(importers.size) == 0)) {
        val start = imports.map(_.pos.startLine).min
        val end = imports.map(_.pos.endLine).max

        Seq(
          doc.input.text.linesIterator.zipWithIndex
            .slice(start, end + 1)
            .filter(_._1.trim.isEmpty)
            .map { case (_, emptyLineNumber) =>
              Patch.lint(
                SyntacticOrganizeImportsWarn(
                  Position.Range(
                    input = doc.input,
                    startLine = emptyLineNumber,
                    startColumn = 0,
                    endLine = emptyLineNumber + 1,
                    endColumn = 0
                  ),
                  "there is empty line in top level imports",
                  severity
                )
              )
            }
            .toList
            .asPatch,
          importers
            .sortBy(importToString)
            .zip(importers)
            .find { case (x1, x2) =>
              importToString(x1) != importToString(x2)
            }
            .map { case (_, value) =>
              Patch.lint(
                SyntacticOrganizeImportsWarn(
                  value.pos,
                  "does not sorted imports",
                  severity
                )
              )
            }
            .asPatch
        ).asPatch
      } else {
        Patch.empty
      }
    }.asPatch
  }
}

case class SyntacticOrganizeImportsWarn(
  override val position: Position,
  override val message: String,
  override val severity: LintSeverity
) extends Diagnostic

class SyntacticOrganizeImportsError extends SyntacticOrganizeImports {
  override val name: RuleName = RuleName(this.getClass.getSimpleName)
  override protected def severity: LintSeverity = LintSeverity.Error
}
