package fix

import fix.ImplicitOnlyMethod.DeclDefOrDefnDef
import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Term
import scala.meta.Type
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

object ImplicitOnlyMethod {
  private sealed abstract class DeclDefOrDefnDef {
    def tparams: List[Type.Param]
    def paramss: List[List[Term.Param]]
  }
  private object DeclDefOrDefnDef {
    def unapply(x: Term): Option[DeclDefOrDefnDef] = PartialFunction.condOpt(x) {
      case t: Defn.Def => DefnDef(t)
      case t: Decl.Def => DeclDef(t)
    }
    case class DeclDef(value: Decl.Def) extends DeclDefOrDefnDef {
      override def tparams = value.tparams
      override def paramss = value.paramss
    }
    case class DefnDef(value: Defn.Def) extends DeclDefOrDefnDef {
      override def tparams = value.tparams
      override def paramss = value.paramss
    }
  }
}

class ImplicitOnlyMethod extends SyntacticRule("ImplicitOnlyMethod") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ DeclDefOrDefnDef(x) if x.tparams.isEmpty =>
        x.paramss.headOption
          .flatMap(_.headOption)
          .filter(_.mods.exists(_.is[Mod.Implicit]))
          .filter(
            _.decltpe.exists(_.is[Type.Name])
          )
          .map { _ =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "implicit param only method",
                position = t.pos,
                severity = LintSeverity.Warning
              )
            )
          }
          .asPatch
    }.asPatch
  }
}
