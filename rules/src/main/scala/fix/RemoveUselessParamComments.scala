package fix

import java.util.Locale
import scala.meta.Decl
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Defn
import scala.meta.Tree
import scala.meta.contrib.AssociatedComments
import scala.meta.contrib.DocToken
import scala.meta.contrib.XtensionCommentOps

class RemoveUselessParamComments extends SyntacticRule("RemoveUselessParamComments") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class =>
        p(t, doc.comments)
      case t: Defn.Def =>
        p(t, doc.comments)
      case t: Decl.Def =>
        p(t, doc.comments)
    }.asPatch
  }

  private[this] def p(t: Tree, comments: AssociatedComments): Patch = {
    comments
      .leading(t)
      .toSeq
      .map { x =>
        val xs = x.docTokens.toList.flatten.map { c =>
          (c.kind, c.name.map(_.toLowerCase(Locale.ROOT)), c.body.map(_.toLowerCase(Locale.ROOT)))
        }.collect {
          case (DocToken.Param, Some(x1), Some(x2)) if x1 == x2 =>
            PartialFunction.condOpt(x.value.linesIterator.zipWithIndex.collect {
              case (str, i) if str.contains(s" ${x1} ") => i
            }.toList) { case List(index) =>
              index
            }
          case (DocToken.Param, Some(x1), _) if x1.contains("@param") && x1.contains('\n') =>
            PartialFunction.condOpt(x.value.linesIterator.zipWithIndex.collect {
              case (str, i) if str.contains(s" ${x1.dropRight("@param".length + 1)}") => i
            }.toList) { case List(index) =>
              index
            }
        }.flatten.toSet

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
