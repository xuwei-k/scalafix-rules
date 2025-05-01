package fix

import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch
import scalafix.{Patch, XtensionOptionPatch}

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.XtensionClassifiable
import scala.meta.transversers.*

class CaseClassImplicit extends SyntacticRule("CaseClassImplicit") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Class
          if t.mods.exists(_.is[Mod.Case]) && t.ctor.paramClauses.headOption.exists(
            _.values.exists(_.mods.exists(_.is[Mod.Implicit]))
          ) =>
        Seq(
          t.mods.find(_.is[Mod.Case]).map(x => Patch.removeTokens(x.tokens)).asPatch,
          t.ctor.paramClauses.headOption.map(
            _.values.filterNot(_.mods.exists(_.is[Mod.ValParam])).map(Patch.addLeft(_, " val ")).asPatch
          ).asPatch
        ).asPatch
    }.asPatch
  }
}

