package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Position
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.typeParamClauseToValues
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class UnusedTypeParams extends SyntacticRule("UnusedTypeParams") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Def if !t.mods.exists(_.is[Mod.Override]) =>
        val typeParams = t.paramClauseGroups.flatMap(_.tparamClause).filter(p => p.cbounds.isEmpty && p.vbounds.isEmpty)
        val typeParamsMap = typeParams.map(t => t.name.value -> t).toMap
        val names = t.collect {
          case x: Term.Name =>
            typeParamsMap.get(x.value)
          case x: Type.Name =>
            typeParamsMap.get(x.value)
          case x: scala.meta.Name =>
            typeParamsMap.get(x.value)
        }.flatten
        names
          .groupBy(_.name.value)
          .values
          .collect { case a :: Nil =>
            Patch.lint(new UnusedTypeParamsWarn(a.pos))
          }
          .asPatch
    }.asPatch
  }
}

class UnusedTypeParamsWarn(override val position: Position) extends Diagnostic {
  override def message = "maybe unused type param"
  override def severity: LintSeverity = LintSeverity.Warning
}
