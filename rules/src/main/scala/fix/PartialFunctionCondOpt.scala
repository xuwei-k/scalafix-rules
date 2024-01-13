package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Tree

class PartialFunctionCondOpt extends SyntacticRule("PartialFunctionCondOpt") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case x => fix0(x).asPatch }.asPatch
  }

  private def fix0(tree: Tree)(implicit doc: SyntacticDocument): Option[Patch] = PartialFunction
    .condOpt(tree) {
      case t @ Term.Match.After_4_4_5(expr, init :+ last, _) if init.nonEmpty =>
        last match {
          case Case(Pat.Wildcard(), None, Term.Name("None")) =>
            val values = init.collect {
              case a @ Case(_, _, Term.Apply.Initial(Term.Name("Some"), x :: Nil)) if x.collect { case a =>
                    fix0(a)
                  }.flatten.isEmpty =>
                a.copy(body = x)
            }
            if (values.lengthCompare(init.size) == 0) {
              Some(
                Patch.replaceTree(
                  t,
                  s"""PartialFunction.condOpt($expr) {
                 |  ${values.mkString("\n  ")}
                 |}""".stripMargin
                )
              )
            } else {
              None
            }
          case _ =>
            None
        }
    }
    .flatten
}
