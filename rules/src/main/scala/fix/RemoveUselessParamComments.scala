package fix

import java.util.Locale
import scala.meta.Stat
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scala.meta.contrib.AssociatedComments
import scala.meta.internal.Scaladoc
import scala.meta.internal.Scaladoc.TagType
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class RemoveUselessParamComments extends SyntacticRule("RemoveUselessParamComments") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Stat.WithCtor =>
        p(t, doc.comments)
      case t: Tree.WithParamClauseGroups =>
        p(t, doc.comments)
    }.asPatch
  }

  private[this] def p(t: Tree, comments: AssociatedComments): Patch = {
    comments
      .leading(t)
      .toSeq
      .map { x =>
        val xs = scala.meta.internal.parsers.ScaladocParser
          .parse(x.syntax)
          .toSeq
          .flatMap(_.para.flatMap(_.terms))
          .collect { case c @ Scaladoc.Tag(TagType.Param, _, _) =>
            (
              c.label.map(_.value.toLowerCase(Locale.ROOT)),
              c.desc.collect { case text: Scaladoc.Text =>
                text.parts.map(_.part.syntax.toLowerCase(Locale.ROOT))
              }.flatten.toList
            )
          }
          .collect {
            case (Some(x1), Seq(x2)) if x1 == x2 =>
              PartialFunction.condOpt(x.value.linesIterator.zipWithIndex.collect {
                case (str, i) if str.contains(" @param ") && str.contains(s" ${x1} ") => i
              }.toList) { case index :: Nil =>
                index
              }
            case (Some(x1), Nil) =>
              PartialFunction.condOpt(x.value.linesIterator.zipWithIndex.collect {
                case (str, i) if str.contains(" @param ") && str.endsWith(s" ${x1}") => i
              }.toList) { case index :: Nil =>
                index
              }
          }
          .flatten
          .toSet

        if (xs.nonEmpty) {
          Patch.replaceToken(
            x,
            x.value.linesIterator.zipWithIndex.filterNot(a => xs(a._2)).map(_._1).mkString("/*", "\n", "*/")
          )
        } else {
          Patch.empty
        }
      }
      .asPatch
  }
}
