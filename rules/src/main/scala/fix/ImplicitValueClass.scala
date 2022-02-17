package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Mod.ValParam
import scala.meta.Ctor
import scala.meta.Term
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Template
import scala.meta.Tree

class ImplicitValueClass extends SyntacticRule("ImplicitValueClass") {

  private[this] def parentIsTopLevelObject(t: Tree): Boolean = {
    // TODO nested object
    val parent = t.parent.flatMap(_.parent)
    val parentParent = parent.flatMap(_.parent)
    parent.exists(_.is[Defn.Object]) && (parentParent.exists(_.is[Pkg]) || parentParent.exists(_.is[Source]))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case c @ Defn.Class(_, _, _, Ctor.Primary(_, _, List(List(p1))), Template(Nil, Nil, _, stats))
          if c.mods.exists(_.is[Mod.Implicit]) && stats.forall(_.is[Defn.Def]) && parentIsTopLevelObject(c) =>
        Seq(
          {
            if (p1.mods.exists(_.is[ValParam])) {
              if (p1.mods.collect { case p: Mod.Private if p.within.is[Term.This] => () }.isEmpty) {
                Patch.empty
              } else {
                // TODO keep other mods?
                Patch.replaceTree(p1, "private val " + p1.copy(mods = Nil).toString)
              }
            } else {
              Patch.addLeft(p1, "private val ")
            }
          },
          Patch.addRight(c.ctor, " extends AnyVal ")
        ).asPatch
    }.asPatch
  }

}
