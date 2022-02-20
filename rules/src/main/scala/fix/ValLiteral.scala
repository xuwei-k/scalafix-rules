package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Defn
import scala.meta.Lit
import scala.meta.Mod
import scala.meta.Pat
import scala.meta.Template
import scala.meta.Tree

class ValLiteral extends SyntacticRule("ValLiteral") {
  object ClassOrTrait {
    def unapply(t: Tree): Option[Template] = PartialFunction.condOpt(t) {
      case c: Defn.Class => c.templ
      case c: Defn.Trait => c.templ
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case ClassOrTrait(template) =>
      template.stats.collect { case v @ Defn.Val(_, List(Pat.Var(name)), _, Lit(_)) =>
        val d = Defn.Def(
          mods = v.mods.filterNot(_.is[Mod.Lazy]),
          name = name,
          tparams = Nil,
          paramss = Nil,
          decltpe = v.decltpe,
          body = v.rhs
        )
        Patch.replaceTree(v, d.toString)
      }.asPatch
    }.asPatch
  }

}
