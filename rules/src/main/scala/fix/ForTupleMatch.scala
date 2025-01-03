package fix

import scala.meta.Enumerator
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ForTupleMatch extends SyntacticRule("ForTupleMatch") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Term.For =>
        t
      case t: Term.ForYield =>
        t
    }.map { t =>
      t.enums
        .zip(t.enums.drop(1))
        .collect {
          case (
                Enumerator.Generator(Pat.Var(tuple1: Term.Name), _),
                tupleVal @ Enumerator.Val(
                  extracted: Pat.Tuple,
                  Term.Name(tuple2)
                )
              ) if tuple1.value == tuple2 && (t.collect { case Term.Name(x) if x == tuple1.value => () }.size == 2) =>
            Seq(
              Patch.replaceTree(tuple1, extracted.toString),
              Patch.removeTokens(tupleVal.tokens)
            ).asPatch
        }
        .asPatch
    }.asPatch
  }
}
