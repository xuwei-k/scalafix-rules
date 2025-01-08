package fix

import scala.meta.Case
import scala.meta.Pat
import scala.meta.Term
import scala.meta.transversers._
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class CatsToValid extends SyntacticRule("CatsToValid") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Match.After_4_9_9(
            _,
            CatsToValid.Cases(method, err),
            Nil
          ) =>
        Patch.replaceTree(t, s"${t.expr}.${method}(${err})")
    }.asPatch
  }
}

object CatsToValid {
  private object InvalidMethod {
    def unapply(value: String): Option[String] = PartialFunction.condOpt(value) {
      case "invalidNec" => "toValidNec"
      case "invalidNel" => "toValidNel"
      case "invalid" => "toValid"
    }
  }

  private object WrapValid {
    def unapply(t: Term): Option[Term.Name] = PartialFunction.condOpt(t) {
      case Term.Apply.After_4_6_0(
            Term.Select(Term.Name("Validated"), Term.Name("valid")),
            Term.ArgClause((x: Term.Name) :: Nil, None)
          ) =>
        x
      case Term.Select(x: Term.Name, Term.Name("valid")) =>
        x
    }
  }

  private object WrapInvalid {
    def unapply(t: Term): Option[(String, Term)] = PartialFunction.condOpt(t) {
      case Term.Apply.After_4_6_0(
            Term.Select(
              Term.Name("Validated"),
              Term.Name(CatsToValid.InvalidMethod(method))
            ),
            Term.ArgClause(err :: Nil, None)
          ) =>
        (method, err)
      case Term.Select(err, Term.Name(CatsToValid.InvalidMethod(method))) =>
        (method, err)
    }
  }

  private object SomeCase {
    def unapply(c: Case): Boolean = PartialFunction.cond(c) {
      case Case(
            Pat.Extract.After_4_6_0(
              Term.Name("Some"),
              Pat.ArgClause(Pat.Var(x1: Term.Name) :: Nil)
            ),
            None,
            WrapValid(x2)
          ) =>
        x1.value == x2.value
    }
  }

  private object NoneCase {
    def unapply(c: Case): Option[(String, Term)] = PartialFunction.condOpt(c) {
      case Case(
            Term.Name("None") | Pat.Wildcard(),
            None,
            WrapInvalid(method, err)
          ) =>
        (method, err)
    }
  }

  private object Cases {
    def unapply(cases: Term.CasesBlock): Option[(String, Term)] = PartialFunction.condOpt(cases.cases) {
      case List(
            CatsToValid.SomeCase(),
            CatsToValid.NoneCase(x1, x2),
          ) =>
        (x1, x2)
      case List(
            CatsToValid.NoneCase(x1, x2),
            CatsToValid.SomeCase(),
          ) =>
        (x1, x2)
    }
  }
}
