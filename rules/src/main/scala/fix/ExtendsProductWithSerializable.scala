package fix

import scala.meta.Ctor
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Template
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.typeParamClauseToValues
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

/**
 * @see [[https://nrinaudo.github.io/scala-best-practices/adts/product_with_serializable.html]]
 * @see [[https://typelevel.org/blog/2018/05/09/product-with-serializable.html]]
 * @see [[https://github.com/scala/bug/issues/9173]]
 */
class ExtendsProductWithSerializable extends SyntacticRule("ExtendsProductWithSerializable") {
  private[this] def emptyConstructor(ctor: Ctor.Primary): Boolean = {
    ctor.mods.isEmpty && ctor.name.value.isEmpty && ctor.paramClauses.isEmpty
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val sealedClasses = doc.tree.collect {
      case t: Defn.Class
          if t.mods.exists(_.is[Mod.Sealed]) && t.mods.exists(
            _.is[Mod.Abstract]
          ) && !t.mods.exists(_.is[Mod.Case]) && t.templ.inits.isEmpty && t.tparamClause.isEmpty =>
        if (emptyConstructor(t.ctor)) {
          t.name -> t.name
        } else {
          t.name -> t.ctor
        }
      case t: Defn.Trait if t.mods.exists(_.is[Mod.Sealed]) && t.templ.inits.isEmpty && t.tparamClause.isEmpty =>
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
