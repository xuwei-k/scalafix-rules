package fix

import scalafix.v1._
import scala.meta._

class ThrowableToNonFatal extends SemanticRule("ThrowableToNonFatal") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect { case c @ Case(Pat.Typed(v, Type.Name("Throwable")), _, _) =>
      List(
        Patch.addGlobalImport(importer"scala.util.control.NonFatal"),
        Patch.replaceTree(
          c.pat,
          s"NonFatal(${v.syntax})"
        )
      ).asPatch
    }.asPatch.atomic
  }
}
