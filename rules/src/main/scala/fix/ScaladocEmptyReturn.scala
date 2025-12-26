package fix

import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scala.meta.internal.Scaladoc
import scala.meta.internal.Scaladoc.TagType
import scala.meta.internal.parsers.ScaladocParser
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ScaladocEmptyReturn extends SyntacticRule("ScaladocEmptyReturn") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Def =>
        p(t)
      case t: Decl.Def =>
        p(t)
      case t: Defn.Macro =>
        p(t)
    }.flatten.map { case (comment, line) =>
      Patch.replaceToken(
        comment,
        comment.value.linesIterator.zipWithIndex.collect { case x if line != x._2 => x._1 }.mkString("/*", "\n", "*/")
      )
    }.asPatch
  }

  private def p(t: Tree)(implicit doc: SyntacticDocument): List[(Token.Comment, Int)] = {
    doc.comments.leading(t).toList.flatMap { x =>
      val hasEmptyReturn =
        ScaladocParser.parse(x.syntax).toSeq.flatMap(_.para.flatMap(_.terms)).exists {
          case c @ Scaladoc.Tag(TagType.Return, _, _) =>
            c.desc.isEmpty
          case _ =>
            false
        }

      if (hasEmptyReturn) {
        PartialFunction.condOpt(
          x.value.linesIterator.zipWithIndex.collect {
            case (str, i) if str.contains(" @return") =>
              i
          }.toList
        ) { case pos :: Nil =>
          x -> pos
        }
      } else {
        None
      }
    }
  }
}
