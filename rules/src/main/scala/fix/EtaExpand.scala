package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Mod
import scala.meta.Term

class EtaExpand extends SyntacticRule("EtaExpand") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Function(
            params,
            Term.Apply(method, args)
          ) if params.nonEmpty =>
        val paramNames = params.map(_.name.value)
        val argNames = args.collect { case Term.Name(x) => x }
        if (
          paramNames == argNames && params.forall(!_.mods.exists(_.is[Mod.Implicit])) && method.collect {
            case Term.Name(a3) if paramNames.contains(a3) => ()
          }.isEmpty
        ) {
          Patch.replaceTree(t, method.toString)
        } else {
          Patch.empty
        }
    }
  }.asPatch
}
