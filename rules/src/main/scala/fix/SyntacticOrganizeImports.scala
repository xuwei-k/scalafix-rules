package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Import
import scala.meta.Pkg
import scala.meta.inputs.Position
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity

class SyntacticOrganizeImports extends SyntacticRule("SyntacticOrganizeImports") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case p: Pkg =>
      // find top-level imports
      val imports = p.stats.dropWhile(!_.is[Import]).takeWhile(_.is[Import]).sortBy(_.pos.startLine)
      // exclude Rename or multi Importees
      val maybeMultiLine = imports.map(_.toString).forall(i => !Set("{", "}", " => ", " as ").exists(i contains _))
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
            .sortBy(_.toString)
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
