package fix

import scala.meta.Enumerator.Generator
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Tree
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.patch.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class SimplifyForYield extends SyntacticRule("SimplifyForYield") {
  override def fix(implicit doc: SyntacticDocument): Patch =
    getPatch(doc.tree).asPatch

  private def getPatch(tree: Tree): Seq[Patch] = {
    tree.collect { case x1: Term.ForYield =>
      val generatorAndRhs = PartialFunction
        .condOpt(x1.enumsBlock.enums) { case List(x2: Generator) =>
          PartialFunction.condOpt(x2.pat) { case x3: Pat.Var =>
            (x3.name.value, x2.rhs)
          }
        }
        .flatten
      val bodyNameOpt = PartialFunction.condOpt(x1.body) { case x2: Term.Name =>
        x2.value
      }

      PartialFunction.condOpt((generatorAndRhs, bodyNameOpt)) {
        case (Some((generator, rhs)), Some(body)) if (generator == body) && getPatch(rhs).isEmpty =>
          if (
            x1.parent.toList
              .flatMap(_.tokens)
              .takeWhile(_.pos.start == x1.tokens.head.pos.start)
              .lastOption
              .exists(_.is[Token.LeftParen])
          ) {
            Patch.replaceTree(x1, s"(${rhs})")
          } else {
            Patch.replaceTree(x1, rhs.toString)
          }
      }
    }.flatten
  }

}
