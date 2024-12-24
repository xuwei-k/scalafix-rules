package fix

import scala.meta.Enumerator.Generator
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.patch.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class SimplifyForYield extends SyntacticRule("SimplifyForYield") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case x1: Term.ForYield =>
        val generatorAndRhs = x1.enumsBlock.enums match {
          case List(x2: Generator) =>
            x2.pat match {
              case x3: Pat.Var =>
                Some((x3.name.value, x2.rhs))
              case _ =>
                None
            }
          case _ =>
            None
        }
        val bodyNameOpt = x1.body match {
          case x2: Term.Name =>
            Some(x2.value)
          case _ =>
            None
        }

        (generatorAndRhs, bodyNameOpt) match {
          case (Some((generator, rhs)), Some(body)) if generator == body =>
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
          case _ =>
            Patch.empty
        }
      case _ =>
        Patch.empty
    }.asPatch
  }

}
