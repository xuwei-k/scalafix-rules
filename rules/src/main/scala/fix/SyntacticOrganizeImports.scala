package fix

import scala.annotation.tailrec
import scala.meta.Import
import scala.meta.Importee
import scala.meta.Importer
import scala.meta.Pkg
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scala.meta.inputs.Position
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

class SyntacticOrganizeImports extends SyntacticRule("SyntacticOrganizeImports") {

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
    importer.pos match {
      case _: Position.Range =>
        val s = importer.syntax
        // for consistency OrganizeImports
        // TODO https://github.com/scalacenter/scalafix/pull/1896 ?
        val wildcardNewStyle = ".*"
        if (s.endsWith(wildcardNewStyle)) {
          s"${s.dropRight(wildcardNewStyle.length)}._"
        } else {
          s
        }
      case Position.None =>
        // for consistency OrganizeImports
        // https://github.com/scalacenter/scalafix/blob/3ca6e5a129bb070bfa/scalafix-rules/src/main/scala/scalafix/internal/rule/OrganizeImports.scala#L798-L821
        val syntax = importer.syntax

        (isCurlyBraced(importer), syntax lastIndexOfSlice " }") match {
          case (_, -1) =>
            syntax
          case (true, index) =>
            syntax.patch(index, "}", 2).replaceFirst("\\{ ", "{")
          case _ =>
            syntax
        }
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case p: Pkg =>
      // find top-level imports
      val imports = collectWhile(p.stats.dropWhile(!_.is[Import])) { case i: Import => i }.sortBy(_.pos.startLine)
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
                  "there is empty line in top level imports"
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
                  "does not sorted imports"
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

  // https://github.com/scalacenter/scalafix/blob/3ca6e5a129bb070bfa/scalafix-rules/src/main/scala/scalafix/internal/rule/OrganizeImports.scala#L975-L980
  private def isCurlyBraced(importer: Importer): Boolean = {
    (importer.importees.size > 1) || {
      importer.importees.exists {
        case _: Importee.Rename =>
          true
        case _: Importee.Unimport =>
          true
        case _ =>
          false
      }
    }
  }
}

case class SyntacticOrganizeImportsWarn(
  override val position: Position,
  override val message: String
) extends Diagnostic {
  override def severity: LintSeverity = LintSeverity.Warning
}
