package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

private object LazyZipSemantic {
  private val zipValues: Set[String] = Set(
    "scala/collection/StrictOptimizedIterableOps",
    "scala/collection/IterableOps",
    "scala/collection/ArrayOps"
  ).map(_ + "#zip().")
}

/**
 * [[https://github.com/scala/scala/blob/v2.13.18/src/library/scala/collection/LazyZipOps.scala]]
 */
class LazyZipSemantic extends SemanticRule("LazyZipSemantic") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(
              Term.Apply.After_4_6_0(
                Term.Select(
                  _,
                  zip @ Term.Name("zip")
                ),
                Term.ArgClause(
                  _ :: Nil,
                  None
                )
              ),
              Term.Name("map" | "flatMap" | "filter" | "exists" | "forall" | "foreach")
            ),
            Term.ArgClause(
              fun :: Nil,
              None
            )
          ) if LazyZipSemantic.zipValues(zip.symbol.value) =>
        PartialFunction
          .condOpt(fun) { case f: Term.PartialFunction =>
            Seq(
              PartialFunction
                .condOpt(f.cases) {
                  case List(
                        pfCase @ Case(
                          Pat.Tuple(
                            List(
                              Pat.Var(_: Term.Name) | Pat.Wildcard(),
                              Pat.Var(_: Term.Name) | Pat.Wildcard()
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
