package fix

import scala.meta.Ctor
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Template
import scala.meta.Type
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class ExtendsProductWithSerializable extends SyntacticRule("ExtendsProductWithSerializable") {
  private[this] def emptyConstructor(ctor: Ctor.Primary): Boolean = {
    ctor.mods.isEmpty && ctor.name.value.isEmpty && ctor.paramss.isEmpty
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val sealedClasses = doc.tree.collect {
      case t: Defn.Class
          if t.mods.exists(_.is[Mod.Sealed]) && t.mods.exists(
            _.is[Mod.Abstract]
          ) && !t.mods.exists(_.is[Mod.Case]) && t.templ.inits.isEmpty && t.tparams.isEmpty =>
        if (emptyConstructor(t.ctor)) {
          t.name -> t.name
        } else {
          t.name -> t.ctor
        }
      case t: Defn.Trait if t.mods.exists(_.is[Mod.Sealed]) && t.templ.inits.isEmpty && t.tparams.isEmpty =>
        if (emptyConstructor(t.ctor)) {
          t.name -> t.name
        } else {
          t.name -> t.ctor
        }
    }
    sealedClasses.map { case (sealedClass, ctorOrName) =>
      def f(templ: Template): Boolean =
        templ.inits.iterator.map(_.tpe).collect { case Type.Name(y) if sealedClass.value == y => () }.nonEmpty

      if (
        doc.tree.collect {
          case t: Defn.Trait if f(t.templ) =>
            ()
        }.isEmpty
      ) {
        val subClasses = doc.tree.collect {
          case t: Defn.Class if f(t.templ) =>
            t.mods
          case t: Defn.Object if f(t.templ) =>
            t.mods
        }
        if (subClasses.nonEmpty && subClasses.forall(_.exists(_.is[Mod.Case]))) {
          Patch.addRight(ctorOrName, " extends Product with Serializable")
        } else {
          Patch.empty
        }
      } else {
        Patch.empty
      }
    }.asPatch
  }

}
