package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Name
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Stat
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class ProtectedInObject extends SyntacticRule("ProtectedInObject") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Object
          if (t.parent.exists(p => p.is[Pkg.Body] || p.is[Source]) || t.parent.isEmpty) && t.templ.inits.isEmpty =>
        t.templ.body.stats.collect { case x: Stat.WithMods =>
          x.mods.collect {
            case m: Mod.Protected if m.within.is[Term.This] || m.within.is[Name.Anonymous] =>
              Patch.replaceTree(m, "private")
          }.asPatch
        }.asPatch
    }.asPatch
  }
}
