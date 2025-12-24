package fix

import fix.FilterNot._
import scala.meta.Term
import scala.meta.Term.ApplyUnary
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

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
      case Term.Apply.After_4_6_0(
            Term.Select(
              _,
              filter @ FilterValue(inverse)
            ),
            Term.ArgClause(
              Term.AnonymousFunction(
                ApplyUnary(
                  op @ Term.Name("!"),
                  _
                )
              ) :: Nil,
              None
            )
          ) =>
        Seq(
          Patch.replaceTree(filter, inverse),
          Patch.removeTokens(op.tokens)
        ).asPatch
      case Term.Apply.After_4_6_0(
            Term.Select(
              _,
              filter @ FilterValue(inverse)
            ),
            Term.ArgClause(
              Term.Function.After_4_6_0(
                Term.ParamClause(_ :: Nil, _),
                ApplyUnary(
                  op @ Term.Name("!"),
                  _
                )
              ) :: Nil,
              None
            )
          ) =>
        Seq(
          Patch.replaceTree(filter, inverse),
          Patch.removeTokens(op.tokens)
        ).asPatch
    }.asPatch
  }
}
