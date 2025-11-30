package fix

import fix.StringRepeat.CharOrString
import fix.StringRepeat.ToUntil
import scala.meta.Lit
import scala.meta.Name
import scala.meta.Term
import scala.meta.Type
import scala.meta.contrib.XtensionTreeOps
import scala.meta.transversers._
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

private object StringRepeat {
  object ToUntil {
    def unapply(x: Term): Option[Int] = PartialFunction
      .condOpt(x) {
        case Term.ApplyInfix.After_4_6_0(
              Lit.Int(x1),
              Term.Name("to"),
              Type.ArgClause(Nil),
              Term.ArgClause(
                Lit.Int(x2) :: Nil,
                None
              )
            ) =>
          x2 - x1 + 1
        case Term.ApplyInfix.After_4_6_0(
              Lit.Int(x1),
              Term.Name("until"),
              Type.ArgClause(Nil),
              Term.ArgClause(
                Lit.Int(x2) :: Nil,
                None
              )
            ) =>
          x2 - x1
      }
      .filter(_ >= 0)
  }

  sealed abstract class CharOrString {
    def asString: String = this match {
      case CharOrString.C(value) =>
        "\"" + value.value + "\""
      case CharOrString.S(value) =>
        value.toString
    }
  }

  object CharOrString {
    final case class C(value: Lit.Char) extends CharOrString
    final case class S(value: Lit.String) extends CharOrString

    def unapply(t: Lit): Option[CharOrString] = PartialFunction.condOpt(t) {
      case x: Lit.Char => C(x)
      case x: Lit.String => S(x)
    }
  }
}

class StringRepeat extends SyntacticRule("StringRepeat") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Select(
                ToUntil(size),
                Term.Name("map")
              ),
              Term.ArgClause(
                List(
                  Term.Function.After_4_6_0(
                    Term.ParamClause(
                      List(
                        Term.Param(
                          Nil,
                          _: Term.Name | Name.Placeholder(),
                          None,
                          None
                        )
                      ),
                      None
                    ),
                    CharOrString(str)
                  )
                ),
                None
              )
            ),
            Term.Name("mkString")
          ) if doc.tree.collectFirst {
            case Term.Apply.After_4_6_0(x, _) if x == t => ()
          }.isEmpty =>
        Patch.replaceTree(t, s"${str.asString}.repeat($size)")
      case t @ Term.Select(
            Term.Apply.After_4_6_0(
              Term.Apply.After_4_6_0(
                Term.Select(
                  Term.Name("List" | "Seq" | "Vector"),
                  Term.Name("fill")
                ),
                Term.ArgClause(
                  List(
                    size
                  ),
                  None
                )
              ),
              Term.ArgClause(
                List(
                  CharOrString(str)
                ),
                None
              )
            ),
            Term.Name("mkString")
          ) if doc.tree.collectFirst {
            case Term.Apply.After_4_6_0(x, _) if x == t => ()
          }.isEmpty =>
        Patch.replaceTree(t, s"${str.asString}.repeat($size)")
    }.asPatch
  }
}
