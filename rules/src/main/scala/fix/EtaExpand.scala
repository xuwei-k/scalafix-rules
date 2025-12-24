package fix

import scala.meta.Mod
import scala.meta.Term
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class EtaExpand extends SyntacticRule("EtaExpand") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Function.After_4_6_0(
            Term.ParamClause(
              params,
              None
            ),
            Term.Apply.After_4_6_0(
              method,
              Term.ArgClause(
                args,
                None
              )
            )
          ) if params.nonEmpty && (params.lengthCompare(args.size) == 0) && params.forall(_.decltpe.isEmpty) =>
        val paramNames = params.map(_.name.value)
        val argNames = args.collect { case Term.Name(x) => x }
        if (
          paramNames == argNames && params.forall(!_.mods.exists(_.is[Mod.Implicit])) && method.collect {
            case Term.Name(a3) if paramNames.contains(a3) => ()
          }.isEmpty
        ) {
          method match {
            case Term.Name(x) if x.headOption.exists(c => 'A' <= c && c <= 'Z') =>
              // avoid object apply
              Patch.empty
            case _ =>
              Patch.replaceTree(t, method.toString)
          }
        } else {
          Patch.empty
        }
    }
  }.asPatch
}
