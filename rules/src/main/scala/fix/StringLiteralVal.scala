package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Defn
import scala.meta.Lit
import scala.meta.Tree

class StringLiteralVal extends SyntacticRule("StringLiteralVal") {

  private[this] def notLocal(t: Tree): Boolean = {
    val parent = t.parent.flatMap(_.parent)
    parent.exists(a => a.is[Defn.Class] || a.is[Defn.Trait])
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Val(Nil, name :: Nil, tpe, str @ Lit.String(_)) if notLocal(t) =>
        tpe match {
          case Some(_) =>
            Patch.replaceTree(t, s"def ${name}: String = ${str}")
          case None =>
            Patch.replaceTree(t, s"def ${name} = ${str}")
        }
    }
  }.asPatch
}
