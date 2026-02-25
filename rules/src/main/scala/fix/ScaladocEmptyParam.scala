package fix

import scala.meta.Ctor
import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scala.meta.internal.Scaladoc
import scala.meta.internal.Scaladoc.TagType
import scala.meta.internal.parsers.ScaladocParser
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ScaladocEmptyParam extends SyntacticRule("ScaladocEmptyParam") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    fix0(doc).map { case (comment, lines) =>
      val linesSet = lines.toSet
      Patch.replaceTree(
        comment,
        comment.pos.text.linesIterator.zipWithIndex.collect { case x if !linesSet(x._2) => x._1 }.mkString("\n")
      )
    }.asPatch
  }

  private[fix] def fix0(implicit doc: SyntacticDocument): List[(Tree.Comments, Seq[Int])] = {
    doc.tree.collect {
      case t: Defn.Class =>
        p(t)
      case t: Defn.Def =>
        p(t)
      case t: Decl.Def =>
        p(t)
      case t: Defn.Macro =>
        p(t)
      case t: Ctor.Secondary =>
        p(t)
      case t: Defn.Enum =>
        p(t)
      case t: Defn.EnumCase =>
        p(t)
    }.flatten
  }

  private def p(t: Tree): List[(Tree.Comments, Seq[Int])] = {
    t.begComment.toList.map { x =>
      val scaladocParamNames =
        ScaladocParser
          .parse(x.pos.text)
          .toSeq
          .flatMap(_.para.flatMap(_.terms))
          .collect {
            case c @ Scaladoc.Tag(TagType.Param, _, _) if c.desc.isEmpty =>
              c.label.map(_.value.trim)
          }
          .flatten

      def getPositions(paramName: String): List[Int] =
        x.pos.text.linesIterator.zipWithIndex.collect {
          case (str, i) if str.contains(" @param ") && str.contains(s" ${paramName}") =>
            i
        }.toList

      x -> scaladocParamNames.flatMap(paramName =>
        PartialFunction.condOpt(getPositions(paramName)) { case pos :: Nil =>
          pos
        }
      )
    }.filter(_._2.nonEmpty)
  }
}
