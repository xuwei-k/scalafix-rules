package fix

import scala.meta.Lit
import scala.meta.Term
import scala.meta.Type
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class Specs2ScalaTest extends SyntacticRule("Specs2ScalaTest") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must" | "should"),
            Nil,
            List(Term.ApplyType(Term.Name("throwA" | "throwAn"), List(Type.Name(y))))
          ) =>
        Patch.replaceTree(t, s"""assertThrows[${y}]{ $x }""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(
              Term.Apply(
                Term.Name("containAnyOf"),
                List(
                  x2
                )
              )
            )
          ) =>
        Patch.replaceTree(t, s"""assert(${x2}.forall(${x1}.toSet))""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(Term.ApplyType(Term.Name("beAnInstanceOf"), List(x2)))
          ) =>
        Patch.replaceTree(t, s"""assert(${x1}.isInstanceOf[${x2}])""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(
              Term.Apply(
                Term.Name("beOneOf"),
                x2
              )
            )
          ) =>
        Patch.replaceTree(t, s"""assert(Seq(${x2.mkString(", ")}).contains(${x1}))""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(Term.Apply(Term.Name("throwA"), List(x2)))
          ) =>
        Patch.replaceTree(t, s"""assert(intercept(${x1}) == ${x2})""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(
              Term.Apply(
                Term.ApplyType(Term.Name("throwA"), List(x2)),
                List(x3)
              )
            )
          ) =>
        Patch.replaceTree(t, s"""assert(intercept[${x2}]{ $x1 }.getMessage == ${x3})""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(
              Term.Apply(
                Term.Select(
                  Term.Name("beNone"),
                  Term.Name("setMessage")
                ),
                List(x2)
              )
            )
          ) =>
        Patch.replaceTree(t, s"""assert(${x1}.isEmpty, ${x2})""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(
              Term.Apply(
                Term.Select(
                  Term.Apply(Term.Name("beSome"), List(x2)),
                  Term.Name("setMessage")
                ),
                List(x3)
              )
            )
          ) =>
        Patch.replaceTree(t, s"""assert(${x1} == Some(${x2}), ${x3})""")
      case Term.Apply(
            Term.Name("assert"),
            List(
              Term.ApplyInfix(
                _,
                x1 @ Term.Name("=="),
                Nil,
                List(x2 @ Lit.Boolean(true))
              )
            )
          ) =>
        Seq(
          Patch.removeTokens(x1.tokens),
          Patch.removeTokens(x2.tokens)
        ).asPatch
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(
              Term.Apply(
                Term.ApplyType(Term.Name("beBetween"), List(_)) | Term.Name("beBetween"),
                List(x2, x3)
              )
            )
          ) =>
        Patch.replaceTree(t, s"""assert((${x2} <= ${x1}) && (${x1} <= ${x3}))""")
      case t @ Term.ApplyInfix(
            Term.ApplyInfix(
              x,
              Term.Name("must"),
              Nil,
              List(Term.Name("not"))
            ),
            Term.Name("be"),
            Nil,
            List(Term.Name("empty"))
          ) =>
        Patch.replaceTree(t, s"""assert(${x}.nonEmpty)""")
      case t @ Term.ApplyInfix(
            x1,
            Term.Name("must"),
            Nil,
            List(Term.Apply(Term.Name("be_>="), x2 :: Nil))
          ) =>
        Patch.replaceTree(t, s"""assert(${x1} >= ${x2})""")
      case t @ Term.ApplyInfix(
            Term.ApplyInfix(
              Term.Name(x1),
              Term.Name("must"),
              Nil,
              List(Term.Name("have"))
            ),
            Term.Name("size"),
            Nil,
            List(x2)
          ) =>
        Patch.replaceTree(t, s"""assert(${x1}.size == ${x2})""")
      case t @ Term.Apply(
            Term.Select(
              Term.ApplyInfix(
                x,
                Term.Name("must"),
                Nil,
                List(Term.Name("be"))
              ),
              Term.Name("equalTo")
            ),
            List(y)
          ) =>
        Patch.replaceTree(t, s"assert(${x} == ${y})")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Name("beSome") | Term.ApplyType(Term.Name("beSome"), List(_)))
          ) =>
        Patch.replaceTree(t, s"assert(${x}.isDefined)")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Apply(Term.Name("beSome"), List(y)))
          ) =>
        Patch.replaceTree(t, s"assert(${x} == Some(${y}))")
      case t @ Term.ApplyInfix(x, Term.Name("must_=="), Nil, List(y)) =>
        Patch.replaceTree(t, s"assert(${x} == ${y})")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("mustEqual" | "shouldEqual"),
            Nil,
            List(y)
          ) =>
        Patch.replaceTree(t, s"assert(${x} == ${y})")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("shouldNotEqual"),
            Nil,
            List(y)
          ) =>
        Patch.replaceTree(t, s"assert(${x} != ${y})")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Name("beNull"))
          ) =>
        Patch.replaceTree(t, s"assert(${x} == null)")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Name("beNone" | "beEmpty"))
          ) =>
        Patch.replaceTree(t, s"assert(${x}.isEmpty)")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(
              Term.Apply(Term.Name("containTheSameElementsAs"), List(y))
            )
          ) =>
        Patch.replaceTree(t, s"assert(${x}.toSet == ${y}.toSet)")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Apply(Term.Name("contain"), List(y)))
          ) =>
        Patch.replaceTree(t, s"assert(${x}.contains($y))")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Apply(Term.Name("be_==" | "be_==="), List(y)))
          ) =>
        Patch.replaceTree(t, s"assert(${x} == ${y})")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must" | "should"),
            Nil,
            List(Term.Name("beTrue"))
          ) =>
        Patch.replaceTree(t, s"assert(${x})")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must" | "should"),
            Nil,
            List(Term.Name("beFalse"))
          ) =>
        Patch.replaceTree(t, s"assert(${x} == false)")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Apply(Term.Name("beEqualTo" | "equalTo" | "be_===" | "be_=="), List(y)))
          ) =>
        Patch.replaceTree(t, s"assert(${x} == ${y})")
      case t @ Term.ApplyInfix(
            x,
            Term.Name("must"),
            Nil,
            List(Term.Apply(Term.Name("size"), List(n)))
          ) =>
        Patch.replaceTree(t, s"assert(${x}.size == ${n})")
    }.asPatch
  }
}
