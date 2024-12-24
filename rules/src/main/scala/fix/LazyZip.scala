package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

/**
  * [[https://github.com/scala/scala/blob/v2.13.12/src/library/scala/collection/LazyZipOps.scala]]
  */
class LazyZip extends SyntacticRule("LazyZip") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.Initial(
            Term.Select(
              Term.Apply.Initial(
                Term.Select(
                  _,
                  zip @ Term.Name("zip")
                ),
                _ :: Nil
              ),
              Term.Name("map" | "flatMap" | "filter" | "exists" | "forall" | "foreach")
            ),
            fun :: Nil
          ) =>
        PartialFunction
          .condOpt(fun) { case f: Term.PartialFunction =>
            Seq(
              PartialFunction
                .condOpt(f.cases) {
                  case List(
                        pfCase @ Case(
                          Pat.Tuple(
                            List(
                              Pat.Var(_: Term.Name),
                              Pat.Var(_: Term.Name)
                            )
                          ),
                          None,
                          _
                        )
                      ) =>
                    Patch.removeTokens(pfCase.tokens.find(_.is[Token.KwCase]))
                }
                .asPatch,
              Patch.replaceTree(zip, "lazyZip")
            ).asPatch
          }
          .asPatch
    }.asPatch
  }
}
