package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Import
import scala.meta.Importee
import scala.meta.Pkg
import scala.meta.inputs.Position
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scala.annotation.tailrec

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

  private def importToString(i: Import): String = {
    val s = i.toString
    if (i.importers.forall(_.importees.forall(!_.is[Importee.Wildcard]))) {
      s
    } else {
      // for consistency OrganizeImports
      // TODO https://github.com/scalacenter/scalafix/pull/1896 ?
      val wildcardNewStyle = ".*"
      if (s.endsWith(wildcardNewStyle)) {
        s"${s.dropRight(wildcardNewStyle.length)}._"
      } else {
        s
      }
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case p: Pkg =>
      // find top-level imports
      val imports = collectWhile(p.stats.dropWhile(!_.is[Import])) { case i: Import => i }.sortBy(_.pos.startLine)
      // exclude Rename or multi Importees
      val maybeMultiLine = imports.forall(_.importers.forall(_.importees.forall(!_.is[Importee.Rename])))
      if (imports.nonEmpty && maybeMultiLine) {
        Seq(
          imports.tail.zip(imports).find { case (x1, x2) => (x1.pos.startLine - x2.pos.startLine) != 1 }.map {
            case (_, value) =>
              Patch.lint(
                SyntacticOrganizeImportsWarn(
                  Position.Range(
                    input = value.pos.input,
                    startLine = value.pos.startLine + 1,
                    startColumn = 0,
                    endLine = value.pos.startLine + 1,
                    endColumn = 0
                  ),
                  "there is empty line in top level imports"
                )
              )
          },
          imports
            .sortBy(importToString)
            .zip(imports)
            .find { case (x1, x2) =>
              x1.toString != x2.toString
            }
            .map { case (_, value) =>
              Patch.lint(
                SyntacticOrganizeImportsWarn(
                  value.pos,
                  "does not sorted imports"
                )
              )
            }
        ).flatten.asPatch
      } else {
        Patch.empty
      }
    }.asPatch
  }
}

case class SyntacticOrganizeImportsWarn(
  override val position: Position,
  override val message: String
) extends Diagnostic {
  override def severity: LintSeverity = LintSeverity.Warning
}
