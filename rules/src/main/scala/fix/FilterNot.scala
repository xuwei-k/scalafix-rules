package fix

import fix.FilterNot._
import scala.meta.Term
import scala.meta.Term.ApplyUnary
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

object FilterNot {
  private object FilterValue {
    def unapply(t: Term.Name): Option[String] = {
      PartialFunction.condOpt(t.value) {
        case "filter" =>
          "filterNot"
        case "filterNot" =>
          "filter"
      }
    }
  }
}

class FilterNot extends SyntacticRule("FilterNot") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply(
            Term.Select(
              _,
              filter @ FilterValue(inverse)
            ),
            Term.AnonymousFunction(
              ApplyUnary(
                op @ Term.Name("!"),
                _
              )
            ) :: Nil
          ) =>
        Seq(
          Patch.replaceTree(filter, inverse),
          Patch.removeTokens(op.tokens)
        ).asPatch
      case Term.Apply(
            Term.Select(
              _,
              filter @ FilterValue(inverse)
            ),
            Term.Function(
              _ :: Nil,
              ApplyUnary(
                op @ Term.Name("!"),
                _
              )
            ) :: Nil
          ) =>
        Seq(
          Patch.replaceTree(filter, inverse),
          Patch.removeTokens(op.tokens)
        ).asPatch
    }.asPatch
  }
}
