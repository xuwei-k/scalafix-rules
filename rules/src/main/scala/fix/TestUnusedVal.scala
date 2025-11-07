package fix

import scala.meta.*
import scala.meta.Defn
import scala.meta.Pat
import scala.meta.Term
import scalafix.Patch
import scalafix.v1.*
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class TestUnusedVal extends SyntacticRule("TestUnusedVal") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case c: Defn.Class if !c.mods.exists(_.is[Mod.Abstract]) =>
        val mockNames = doc.tree.collect {
          case t @ Defn.Val(
              Nil,
              List(Pat.Var(Term.Name(valName))),
              _,
              _
            ) =>
            (t, valName)
        }

        val names = doc.tree
          .collect {
            case x: Term.Name => x
          }
          .groupBy(_.value)
          .filter(_._2.sizeIs == 1)
          .keySet

        mockNames
          .filter(x => names(x._2))
          .map(_._1)
          .map { t =>
            Patch.removeTokens(t.tokens)
          }
          .asPatch
    }
  }.asPatch
}
