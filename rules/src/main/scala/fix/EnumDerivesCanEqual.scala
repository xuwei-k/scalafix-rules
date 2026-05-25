package fix

import scala.meta._
import scalafix.v1._

class EnumDerivesCanEqual extends SyntacticRule("EnumDerivesCanEqual") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Enum if !t.templ.derives.exists {
            case Type.Name("CanEqual") | Type.Select(
                  Term.Name("scala"),
                  Type.Name("CanEqual")
                ) =>
              true
            case _ =>
              false
          } =>
        t.templ.derives.lastOption match {
          case Some(p) =>
            Patch.addRight(p, ", CanEqual")
          case None =>
            Patch.addRight(
              t.templ.inits.lastOption.getOrElse(
                Seq(
                  t.ctor,
                  t.tparamClause,
                ).find(x => x.pos.start < x.pos.end).getOrElse(t.name)
              ),
              " derives CanEqual"
            )
        }
    }.asPatch
  }
}
