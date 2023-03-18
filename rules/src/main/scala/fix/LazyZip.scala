package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

/**
  * [[https://github.com/scala/scala/blob/v2.13.10/src/library/scala/collection/LazyZipOps.scala]]
  */
class LazyZip extends SyntacticRule("LazyZip") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply(
            Term.Select(
              Term.Apply(
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
        fun match {
          case f: Term.Function if f.params.size <= 1 =>
            Patch.empty
          case Term.Block(List(f: Term.Function)) if f.params.size <= 1 =>
            Patch.empty
          case _: Term.AnonymousFunction =>
            Patch.empty
          case _ =>
            Seq(
              PartialFunction
                .condOpt(fun) {
                  case Term.PartialFunction(
                        List(
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
                        )
                      ) =>
                    Patch.removeTokens(pfCase.tokens.find(_.is[Token.KwCase]))
                }
                .asPatch,
              Patch.replaceTree(zip, "lazyZip")
            ).asPatch
        }
    }.asPatch
  }
}
