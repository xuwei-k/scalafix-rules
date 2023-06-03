package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta._

/**
 * `implicit val foo = new Foo`
 * to
 * `implicit val foo: Foo = new Foo`
 */
class AddExplicitImplicitTypes extends SyntacticRule("AddExplicitImplicitTypes") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t1: Defn.Val if t1.mods.exists(_.is[Mod.Implicit]) && t1.decltpe.isEmpty =>
        t1.rhs match {
          case t2: Term.New =>
            t2.init.tpe match {
              case name: Type.Name =>
                Patch.replaceTree(
                  t1,
                  t1.copy(
                    decltpe = Some(name)
                  ).toString
                )
              case _ => // TODO if there is type params
                Patch.empty
            }
          case t2: Term.NewAnonymous =>
            t2.templ.inits.map(_.tpe) match {
              case (tpe @ Type.Name(_)) :: Nil =>
                Patch.replaceTree(
                  t1,
                  t1.copy(
                    decltpe = Some(tpe)
                  ).toString
                )
              case _ =>
                Patch.empty
            }
          case _ =>
            Patch.empty
        }
    }.asPatch
  }
}
