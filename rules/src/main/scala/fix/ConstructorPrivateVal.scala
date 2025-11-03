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
        val names: Set[String] = Seq(
          t.collect {
            case Term.Select(
                  _,
                  n: Term.Name
                ) =>
              n.value
          },
          doc.tree.collect {
            case o: Defn.Object if o.name.value == t.name.value =>
              o.collect {
                case Term.Select(
                      _,
                      n: Term.Name
                    ) =>
                  n.value
              }
          }.flatten
        ).flatten.toSet

        t.ctor.paramClauses
          .flatMap(_.values)
          .collect {
            case p @ Term.Param(
                  List(
                    m1 @ Mod.Private(Name.Anonymous() | Term.This(Name.Anonymous())),
                    m2 @ Mod.ValParam()
                  ),
                  _,
                  _,
                  _
                ) if !names(p.name.value) =>
              Patch.removeTokens(m1.tokens ++ m2.tokens)
          }
          .asPatch
    }.asPatch
  }
}
