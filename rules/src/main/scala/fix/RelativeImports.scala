package fix

import scala.annotation.tailrec
import scalafix.Diagnostic
import scalafix.Patch
import scala.meta.Import
import scala.meta.Importer
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Stat
import scala.meta.Term
import scala.meta.Tree
import scala.meta.inputs.Position
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticRule
import scalafix.v1.SemanticDocument
import scalafix.v1.XtensionTreeScalafix

class RelativeImports extends SemanticRule("RelativeImports") {
  @tailrec private def topQualifierOf(term: Term): Term.Name =
    term match {
      case Term.Select(qualifier, _) => topQualifierOf(qualifier)
      case name: Term.Name => name
    }

  @tailrec private def collectGlobalImports(tree: Tree): Seq[Import] = {
    def extractImports(stats: Seq[Stat]): Seq[Import] = {
      stats.takeWhile(_.is[Import]).map { case i: Import => i }
    }

    tree match {
      case Source(Seq(p: Pkg)) => collectGlobalImports(p)
      case Pkg(_, Seq(p: Pkg)) => collectGlobalImports(p)
      case Source(stats) => extractImports(stats)
      case Pkg(_, stats) => extractImports(stats)
      case _ => Nil
    }
  }

  private def isFullyQualified(importer: Importer)(implicit doc: SemanticDocument): Boolean = {
    val topQualifier = topQualifierOf(importer.ref)
    val topQualifierSymbol = topQualifier.symbol
    val owner = topQualifierSymbol.owner

    owner.isRootPackage ||
    owner.isEmptyPackage ||
    (topQualifier.value == "_root_") ||
    topQualifierSymbol.isNone
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    collectGlobalImports(doc.tree)
      .flatMap(_.importers)
      .filterNot(isFullyQualified)
      .map { i =>
        Patch.lint(RelativeImportsWarn(i.pos))
      }
      .asPatch
  }
}

case class RelativeImportsWarn(override val position: Position) extends Diagnostic {
  override def message = "don't use relative import"
  override def severity = LintSeverity.Warning
}
