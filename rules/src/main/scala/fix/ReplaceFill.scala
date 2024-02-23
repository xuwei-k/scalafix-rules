package fix

import scala.meta.Enumerator
import scala.meta.Lit
import scala.meta.Name
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ReplaceFill extends SyntacticRule("ReplaceFill") {

  private object ToUntil {
    object CollectionType {
      def unapply(method: String): Option[String] = PartialFunction.condOpt(method) {
        case "toList" => "List"
        case "toSeq" => "Seq"
        case "toVector" => "Vector"
        case "toArray" => "Array"
        case "iterator" => "Iterator"
      }
    }

    object Size {
      def unapply(x: Term): Option[Int] = PartialFunction.condOpt(x) {
        case Term.ApplyInfix.Initial(Lit.Int(x1), Term.Name("to"), _, Lit.Int(x2) :: Nil) =>
          x2 - x1 + 1
        case Term.ApplyInfix.Initial(Lit.Int(x1), Term.Name("until"), _, Lit.Int(x2) :: Nil) =>
          x2 - x1
      }
    }

    def unapply(t: Term): Option[(Int, String)] =
      PartialFunction.condOpt(t) {
        case Size(n) =>
          (n, "Seq")
        case Term.Select(
              Size(n),
              Term.Name(CollectionType(c))
            ) =>
          (n, c)
      }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            Term.Select(
              ToUntil(size, tpe),
              Term.Name("map")
            ),
            Term.ArgClause(Term.Function.After_4_6_0(Term.ParamClause(param :: Nil, _), body) :: Nil, _),
          ) if param.is[Name.Anonymous] || body.collect {
            case Name(n) if n == param.name.value => ()
          }.isEmpty =>
        Patch.replaceTree(t, s"${tpe}.fill(${size}){$body}")
      case t @ Term.ForYield(
            List(
              Enumerator.Generator(
                Pat.Var(n @ Term.Name(_)),
                ToUntil(size, tpe)
              )
            ),
            body
          ) if body.collect {
            case Name(x) if x == n.value => ()
          }.isEmpty =>
        Patch.replaceTree(t, s"${tpe}.fill(${size}){$body}")
      case t @ Term.ForYield(
            List(
              Enumerator.Generator(
                Pat.Wildcard(),
                ToUntil(size, tpe)
              )
            ),
            body
          ) =>
        Patch.replaceTree(t, s"${tpe}.fill(${size}){$body}")
    }.asPatch
  }
}
