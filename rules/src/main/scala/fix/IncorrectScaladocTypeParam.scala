package fix

import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Term
import scala.meta.Tree
import scala.meta.Type
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.contrib.DocToken
import scala.meta.contrib.XtensionCommentOps
import scala.meta.inputs.Position
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity

class IncorrectScaladocTypeParam extends SyntacticRule("IncorrectScaladocTypeParam") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    fix0(doc).map(Patch.lint).asPatch
  }
  private[fix] def fix0(implicit doc: SyntacticDocument): List[Diagnostic] = {
    doc.tree.collect {
      case t: Decl.Def =>
        p(t, t.tparams)
      case t: Decl.Given =>
        p(t, t.tparams)
      case t: Defn.Class =>
        p(t, t.tparams)
      case t: Defn.Trait =>
        p(t, t.tparams)
      case t: Defn.Def =>
        p(t, t.tparams)
      case t: Defn.Macro =>
        p(t, t.tparams)
      case t: Defn.Given =>
        p(t, t.tparams)
      case t: Defn.GivenAlias =>
        p(t, t.tparams)
      case t: Defn.ExtensionGroup =>
        p(t, t.tparams)
      case t: Defn.Enum =>
        p(t, t.tparams)
      case t: Defn.EnumCase =>
        p(t, t.tparams)
    }.flatten
  }

  private def p(t: Tree, paramss: List[Type.Param])(implicit doc: SyntacticDocument): List[Diagnostic] = {
    val names = paramss.map(_.name.value.trim).toSet

    doc.comments.leading(t).toList.flatMap { x =>
      x.docTokens.toList.flatten.flatMap { c =>
        PartialFunction
          .condOpt(c.kind) { case DocToken.TypeParam =>
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
                  message = "incorrect @tparam",
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
