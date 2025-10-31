package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Name
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ConstructorPrivateVal extends SyntacticRule("ConstructorPrivateVal") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class if !t.mods.exists(m => m.is[Mod.Case] || m.is[Mod.Implicit]) =>
        t.ctor.paramClauses
          .flatMap(_.values)
          .collect {
            case Term.Param(
                  List(
                    m1 @ Mod.Private(Name.Anonymous() | Term.This(Name.Anonymous())),
                    m2 @ Mod.ValParam()
                  ),
                  _,
                  _,
                  _
                ) =>
              Patch.removeTokens(m1.tokens ++ m2.tokens)
          }
          .asPatch
    }.asPatch
  }
}
