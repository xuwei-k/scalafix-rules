package fix

import scala.annotation.tailrec
import scala.meta.Ctor
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Mod.ValParam
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Template
import scala.meta.Term
import scala.meta.Tree
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ImplicitValueClass extends SyntacticRule("ImplicitValueClass") {

  private[this] def allParentIsObject(t: Tree): Boolean = {
    @tailrec
    def loop(a: Tree): Boolean = {
      a.parent match {
        case Some(x) =>
          if (
            x.is[Template] || x.is[Defn.Object] || x.is[Pkg] || x.is[Source] || x.is[Template.Body] || x.is[Pkg.Body]
          ) {
            loop(x)
          } else {
            false
          }
        case None =>
          true
      }
    }
    loop(t)
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case c @ Defn.Class.After_4_6_0(
            _,
            _,
            _,
            Ctor.Primary.Initial(_, _, List(p1 :: Nil)),
            Template.Initial(Nil, Nil, _, stats)
          )
          if c.mods.exists(_.is[Mod.Implicit]) && stats.forall(_.is[Defn.Def]) && allParentIsObject(c) && !p1.decltpe
            .forall(_.is[Type.ByName]) && c.tparamClause.values.forall(_.bounds.context.isEmpty) =>
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
