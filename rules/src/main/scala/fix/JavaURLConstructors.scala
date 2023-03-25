package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Init
import scala.meta.Type
import scala.meta.XtensionQuasiquoteImporter

class JavaURLConstructors extends SyntacticRule("JavaURLConstructors") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.New(
            Init(
              Type.Name("URL") | Type.Select(
                Term.Select(
                  Term.Name("java"),
                  Term.Name("net")
                ),
                Type.Name("URL")
              ),
              _,
              (_ :: Nil) :: Nil
            )
          ) =>
        Seq(
          Patch.addGlobalImport(importer"java.net.URI"),
          Patch.replaceTree(t.init.tpe, "URI"),
          Patch.addRight(t, ".toURL")
        ).asPatch
    }.asPatch
  }
}
