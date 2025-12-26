package fix

import fix.SideEffectingNullaryMethod.UnitType
import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

private object SideEffectingNullaryMethod {
  private object UnitType {
    def unapply(t: Type): Boolean = PartialFunction.cond(t) {
      case Type.Select(
            Term.Select(
              Term.Name("_root_"),
              Term.Name("scala")
            ),
            Type.Name("Unit")
          ) =>
        true
      case Type.Select(
            Term.Name("scala"),
            Type.Name("Unit")
          ) =>
        true
      case Type.Name("Unit") =>
        true
    }
  }
}

/**
 * Scala 3 compiler does not report this warning
 * [[https://github.com/scala/scala/blob/2b398f889eee2b80a6d479159369671905fd1ce9/src/compiler/scala/tools/nsc/typechecker/RefChecks.scala#L1921]]
 */
class SideEffectingNullaryMethod extends SyntacticRule("SideEffectingNullaryMethod") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Def.After_4_7_3(
            _,
            _,
            Nil,
            Some(
              UnitType()
            ),
            _
          ) if !t.mods.exists(_.is[Mod.Override]) =>
        t.name
      case t @ Decl.Def.After_4_7_3(
            _,
            _,
            Nil,
            UnitType(),
          ) if !t.mods.exists(_.is[Mod.Override]) =>
        t.name
    }.map(t => Patch.addRight(t, "()")).asPatch
  }
}
