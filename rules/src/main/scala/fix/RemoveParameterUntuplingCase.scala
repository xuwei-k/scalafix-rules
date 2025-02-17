package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.v1.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

/**
 * [[https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling.html]]
 * [[https://docs.scala-lang.org/scala3/reference/other-new-features/parameter-untupling-spec.html]]
 */
class RemoveParameterUntuplingCase extends SyntacticRule("RemoveParameterUntuplingCase") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.PartialFunction(
            List(
              c @ Case(
                Pat.Tuple(
                  values
                ),
                None,
                _
              )
            )
          ) if values.forall {
            case Pat.Var(_: Term.Name) => true
            case _ => false
          } =>
        c.tokens
          .find(_.is[Token.KwCase])
          .map(
            Patch.removeToken
          )
          .asPatch
    }.asPatch
  }
}
