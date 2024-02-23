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
        p(t, t.paramClauses)
      case t: Decl.Def =>
        p(t, t.paramClauses)
      case t: Defn.Macro =>
        p(t, t.paramClauses)
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
      x.docTokens.toList.flatten.flatMap { c =>
        PartialFunction
          .condOpt(c.kind) { case DocToken.Param =>
            c.name
          }
          .flatten
          .map(_.trim)
          .flatMap { paramName =>
            if (names(paramName)) {
              None
            } else {
              PartialFunction.condOpt(x.value.linesIterator.zipWithIndex.collect {
                case (str, i) if str.contains(s" ${paramName} ") => (str.length, i)
              }.toList) { case List((len, index)) =>
                Diagnostic(
                  id = "",
                  message = "incorrect @param",
                  position = {
                    val line = x.pos.startLine + index
                    Position.Range(
                      input = doc.input,
                      startLine = line,
                      startColumn = 0,
                      endLine = line,
                      endColumn = len
                    )
                  },
                  severity = LintSeverity.Warning
                )
              }
            }
          }
      }
    }
  }
}
