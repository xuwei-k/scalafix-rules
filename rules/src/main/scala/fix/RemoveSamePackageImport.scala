package fix

import scalafix.v1._
import scala.meta._

class RemoveSamePackageImport extends SyntacticRule("RemoveSamePackageImport") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val pkgOpt = doc.tree.collect { case pkg: Pkg =>
      pkg
    }.headOption

    pkgOpt.map { pkg =>
      val ref = s"${pkg.ref.toString()}"
      doc.tree.collect {
        case _: Importee.Rename =>
          Patch.empty
        case t: Importee =>
          val p = t.parent.get
          val children = p.children
          if (ref == children.head.toString()) {
            val last = children.last.toString()
            if (last.matches("^[a-z]+$")) {
              // Keep sub package import
              Patch.empty
            } else {
              Patch.replaceTree(p.parent.get, "")
            }
          } else {
            Patch.empty
          }
      }.asPatch
    }.asPatch.atomic
  }
}
