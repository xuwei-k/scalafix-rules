package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Mod
import scala.meta.Term

class ReplacePlaceholder extends SyntacticRule("ReplacePlaceholder") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Function.Initial(Term.Param(mods, Term.Name(a1), None, _) :: Nil, Term.Select(Term.Name(a2), x))
          if a1 == a2 && !mods.exists(_.is[Mod.Implicit]) =>
        Patch.replaceTree(t, s"_.$x")
      case t @ Term.Function.Initial(
            Term.Param(mods, Term.Name(a1), None, _) :: Nil,
            Term.Apply.Initial(Term.Select(Term.Name(a2), method), x)
          ) if a1 == a2 && !mods.exists(_.is[Mod.Implicit]) && x.forall(_.collect {
            case Term.Name(a3) if a3 == a1 => ()
          }.isEmpty) =>
        Patch.replaceTree(t, s"_.${method}(${x.mkString(", ")})")
      case t @ Term.Function.Initial(
            Term.Param(mods, Term.Name(a1), None, _) :: Nil,
            Term.ApplyInfix.Initial(Term.Select(Term.Name(a2), method), op, Nil, x :: Nil)
          ) if a1 == a2 && !mods.exists(_.is[Mod.Implicit]) && x.collect {
            case Term.Name(a3) if a3 == a1 => ()
          }.isEmpty =>
        Patch.replaceTree(t, s"_.${method} ${op} ${x}")
    }
  }.asPatch
}
