package fix

import scalafix.lint.LintSeverity
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Ctor
import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Tree
import scala.meta.contrib._
import scala.meta.inputs.Position

class InvalidScaladocParam extends SyntacticRule("InvalidScaladocParam") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Decl.Def =>
        check(
          tree = t,
          srcParams = t.paramss.flatten.map(_.name.value).toSet,
          comments = doc.comments
        )
      case t: Defn.Def =>
        check(
          tree = t,
          srcParams = t.paramss.flatten.map(_.name.value).toSet,
          comments = doc.comments
        )
      case t: Ctor.Primary =>
        check(
          tree = t,
          srcParams = t.paramss.flatten.map(_.name.value).toSet,
          comments = doc.comments
        )
      case t: Ctor.Secondary =>
        check(
          tree = t,
          srcParams = t.paramss.flatten.map(_.name.value).toSet,
          comments = doc.comments
        )
      case t: Defn.ExtensionGroup =>
        check(
          tree = t,
          srcParams = t.paramss.flatten.map(_.name.value).toSet,
          comments = doc.comments
        )
      case t: Defn.Macro =>
        check(
          tree = t,
          srcParams = t.paramss.flatten.map(_.name.value).toSet,
          comments = doc.comments
        )
    }.asPatch
  }

  private def check(tree: Tree, srcParams: Set[String], comments: AssociatedComments) = {
    val notExists = comments
      .leading(tree)
      .flatMap(_.docTokens.toList.flatten)
      .filter(_.kind == DocToken.Param)
      .flatMap(_.name)
      .filterNot(srcParams)
    if (notExists.nonEmpty) {
      Patch.lint(InvalidScaladocParamWarn(tree.pos, names = notExists.toSeq.sorted))
    } else {
      Patch.empty
    }
  }
}

case class InvalidScaladocParamWarn(override val position: Position, names: Seq[String]) extends Diagnostic {
  override def message  = s"invalid @param name ${names.map("`" + _ + "`").mkString(", ")}"
  override def severity = LintSeverity.Warning
}
