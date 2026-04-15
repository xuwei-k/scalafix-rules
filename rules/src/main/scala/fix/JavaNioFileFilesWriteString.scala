package fix

import scala.meta.Lit
import scala.meta.Term
import scala.meta.transversers._
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class JavaNioFileFilesWriteString extends SyntacticRule("JavaNioFileFilesWriteString") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case Term.Apply.After_4_6_0(
            Term.Select(
              Term.Name("Files") | Term.Select(
                Term.Select(
                  Term.Select(
                    Term.Name("java"),
                    Term.Name("nio")
                  ),
                  Term.Name("file")
                ),
                Term.Name("Files")
              ),
              write @ Term.Name("write")
            ),
            Term.ArgClause(
              List(
                _,
                bytes @ Term.Apply.After_4_6_0(
                  Term.Select(
                    str,
                    Term.Name("getBytes")
                  ),
                  Term.ArgClause(
                    List(
                      Term.Name("UTF_8") | Term.Select(
                        Term.Name("StandardCharsets"),
                        Term.Name("UTF_8")
                      ) | Lit.String("UTF-8")
                    ),
                    None
                  )
                )
              ),
              None
            )
          ) =>
        Seq(
          Patch.replaceTree(write, "writeString"),
          Patch.replaceTree(bytes, str.toString)
        ).asPatch
    }.asPatch
  }
}
