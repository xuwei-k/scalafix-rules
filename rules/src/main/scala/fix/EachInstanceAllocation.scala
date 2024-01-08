package fix

import scala.meta.Defn
import scala.meta.Term
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class EachInstanceAllocation extends SyntacticRule("EachInstanceAllocation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case clazz: Defn.Class =>
      val names1 = clazz.ctor.paramClauses.flatMap(_.map(_.name.value)).toSet
      val names2 = clazz.templ.inits.flatMap(_.argClauses.flatMap(_.values.collect { case Term.Name(x) => x }))
      val constant = names2.filterNot(names1)
      if (constant.nonEmpty) {
        Patch.lint(
          Diagnostic(
            id = "",
            message = s"${constant}",
            position = clazz.templ.inits.head.pos,
            severity = LintSeverity.Warning
          )
        )
      } else {
        Patch.empty
      }
    }.asPatch
  }
}
