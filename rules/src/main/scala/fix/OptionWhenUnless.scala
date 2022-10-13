package fix

import scala.meta.Term
import scala.meta.Term.Block
import scala.meta.Term.If
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

object OptionWhenUnless {
  private object SomeValue {
    def unapply(x: Term): Option[Term] = PartialFunction.condOpt(x) {
      case Block(Term.Apply(Term.Name("Some"), value :: Nil) :: Nil) =>
        value
      case Term.Apply(Term.Name("Some"), value :: Nil) =>
        value
    }
  }

  private object NoneValue {
    def unapply(x: Term): Boolean = x match {
      case Block(Term.Name("None") :: Nil) =>
        true
      case Term.Name("None") =>
        true
      case _ =>
        false
    }
  }
}

class OptionWhenUnless extends SyntacticRule("OptionWhenUnless") {
  import OptionWhenUnless._

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ If(condition, SomeValue(value), NoneValue()) =>
        Patch.replaceTree(t, s"Option.when(${condition})($value)")
      case t @ If(condition, NoneValue(), SomeValue(value)) =>
        Patch.replaceTree(t, s"Option.unless(${condition})($value)")
    }.asPatch
  }
}
