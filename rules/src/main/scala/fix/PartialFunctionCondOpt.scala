package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term

class PartialFunctionCondOpt extends SyntacticRule("PartialFunctionCondOpt") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_4_5(expr, init :+ last, _) if init.nonEmpty =>
        last match {
          case Case(Pat.Wildcard(), None, Term.Name("None")) =>
            val values = init.collect { case a @ Case(_, _, Term.Apply.Initial(Term.Name("Some"), x :: Nil)) =>
              a.copy(body = x)
            }
            if (values.lengthCompare(init.size) == 0) {
              Patch.replaceTree(
                t,
                s"""PartialFunction.condOpt($expr) {
                   |  ${values.mkString("\n  ")}
                   |}""".stripMargin
              )
            } else {
              Patch.empty
            }
          case _ =>
            Patch.empty
        }
    }.asPatch
  }
}
