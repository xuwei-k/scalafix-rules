package fix

import scala.collection.compat._
import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Stat
import scala.meta.Tree
import scala.meta.transversers._
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object UnmooredDocComment {
  private object Unmoored {
    def unapply(t: Tree): Boolean = t match {
      case _: Stat.WithTemplate | _: Defn | _: Mod | _: Decl =>
        false
      case _ =>
        true
    }
  }
}

/**
 * [[https://github.com/scala/scala/blob/4b124f211b661d/src/scaladoc/scala/tools/nsc/doc/ScaladocAnalyzer.scala#L175]]
 */
class UnmooredDocComment extends SyntacticRule("UnmooredDocComment") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ UnmooredDocComment.Unmoored() =>
      doc.comments.leading(t).toSeq.filter(_.value.startsWith("*")).map(_ -> t)
    }.flatten
      .groupBy(_._1)
      .view
      .mapValues(
        _.map(_._2).maxBy(t => (t.pos.end - t.pos.start, t.pos.end))
      )
      .toSeq
      .sortBy(x => (x._1.pos.start, x._1.pos.end))
      .map { case (comment, tree) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = s"unmoored doc comment for `${treeName(tree)}`",
            position = comment.pos,
            severity = LintSeverity.Warning
          )
        )
      }
      .asPatch
  }

  private def treeName(t: Tree): String = {
    t match {
      case x: scala.Product =>
        x.productPrefix
      case _ =>
        t.getClass.getName
    }
  }
}
