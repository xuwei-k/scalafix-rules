package fix

import scala.meta.Ctor
import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Term
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scala.meta.contrib.DocToken
import scala.meta.contrib.XtensionCommentOps
import scala.meta.inputs.Position
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class IncorrectScaladocParam extends SyntacticRule("IncorrectScaladocParam") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    fix0(doc).map(Patch.lint).asPatch
  }
  private[fix] def fix0(implicit doc: SyntacticDocument): List[Diagnostic] = {
    doc.tree.collect {
      case t: Defn.Class =>
        p(t, t.ctor.paramClauses)
      case t: Defn.Def =>
        p(t, t.paramClauseGroups.flatMap(_.paramClauses))
      case t: Decl.Def =>
        p(t, t.paramClauseGroups.flatMap(_.paramClauses))
      case t: Defn.Macro =>
        p(t, t.paramClauseGroups.flatMap(_.paramClauses))
      case t: Ctor.Secondary =>
        p(t, t.paramClauses)
      case t: Defn.Enum =>
        p(t, t.ctor.paramClauses)
      case t: Defn.EnumCase =>
        p(t, t.ctor.paramClauses)
    }.flatten
  }

  private def p(t: Tree, paramsClauses: Seq[Term.ParamClause])(implicit doc: SyntacticDocument): List[Diagnostic] = {
    val names = paramsClauses.flatMap(_.values.map(_.name.value.trim)).toSet

    doc.comments.leading(t).toList.flatMap { x =>
      val scaladocParamNames = x.docTokens.toList.flatten.flatMap(c =>
        PartialFunction
          .condOpt(c.kind) { case DocToken.Param =>
            c.name
          }
          .flatten
          .map(_.trim)
      )
      val duplicateNames = scaladocParamNames.groupBy(identity).filter(_._2.size > 1).map(_._1).filter(names)

      def getPositions(paramName: String): List[Position] =
        x.value.linesIterator.zipWithIndex.collect {
          case (str, i) if str.contains(s" ${paramName} ") => (str.length, i)
        }.toList.map { case (len, index) =>
          val line = x.pos.startLine + index
          Position.Range(
            input = doc.input,
            startLine = line,
            startColumn = 0,
            endLine = line,
            endColumn = len
          )
        }

      Seq(
        duplicateNames.flatMap(x => getPositions(x).map(x -> _)).map { case (duplicateName, pos) =>
          Diagnostic(
            id = "",
            message = s"duplicate @param ${duplicateName}",
            position = pos,
            severity = LintSeverity.Warning
          )
        },
        scaladocParamNames.flatMap(paramName =>
          if (names(paramName)) {
            Nil
          } else {
            PartialFunction
              .condOpt(getPositions(paramName)) { case pos :: Nil =>
                pos
              }
              .map { pos =>
                Diagnostic(
                  id = "",
                  message = "incorrect @param",
                  position = pos,
                  severity = LintSeverity.Warning
                )
              }
          }
        )
      ).flatten
    }
  }
}
