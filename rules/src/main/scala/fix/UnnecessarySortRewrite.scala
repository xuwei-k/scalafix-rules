package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Term

class UnnecessarySortRewrite extends SyntacticRule("UnnecessarySortRewrite") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(
            Term.Apply(Term.Select(x1, Term.Name("sortBy")), List(x2)),
            Term.Name(methodName)
          ) if UnnecessarySort.map.contains(methodName) =>
        // TODO add `import scala.collection.compat._`
        // if Scala 2.12 and minByOption, minByOption

        Patch.replaceTree(
          t,
          Term.Apply(Term.Select(x1, Term.Name(UnnecessarySort.map(methodName))), List(x2)).toString,
        )
    }.asPatch
  }
}
