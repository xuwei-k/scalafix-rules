package fix

import scala.meta._
import scalafix.v1._

class ObjectEmptyParamMethod extends SyntacticRule("ObjectEmptyParamMethod") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Defn.Object =>
      t.templ.body.stats.collect {
        case x: Defn.Def
            if x.paramClauseGroups.isEmpty && x.mods.isEmpty && !x.body.is[Lit] && !x.body.is[Term.Name] && !x.body
              .is[Term.This] && !PartialFunction.cond(x.body) {
              case Term.Select(
                    _: Term.Name,
                    _: Term.Name
                  ) =>
                true
            } =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "",
              position = x.pos
            )
          )
      }.asPatch
    }.asPatch
  }
}
