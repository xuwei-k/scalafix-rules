package fix

import fix.JavaNioFileFilesWriteString.CharsetValue
import scala.meta.Lit
import scala.meta.Term
import scala.meta.transversers._
import scalafix.Patch
import scalafix.XtensionOptionPatch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

object JavaNioFileFilesWriteString {
  private sealed abstract class CharsetValue

  private object CharsetValue {

    case object UTF8 extends CharsetValue

    final case class OtherString(value: String) extends CharsetValue

    final case class OtherCharset(value: Term.Select) extends CharsetValue

    def unapply(t: Term): Option[CharsetValue] = PartialFunction.condOpt(t) {
      case Term.Name("UTF_8") | Term.Select(
            Term.Name("StandardCharsets"),
            Term.Name("UTF_8")
          ) | Lit.String("UTF-8") =>
        UTF8
      case Lit.String(other) =>
        OtherString(other)
      case x @ Term.Select(
            Term.Name("StandardCharsets"),
            _: Term.Name
          ) =>
        OtherCharset(x)
    }
  }
}

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
                      CharsetValue(c)
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
          Patch.replaceTree(bytes, str.toString),
          PartialFunction
            .condOpt(c) {
              case CharsetValue.OtherString(other) =>
                Patch.addRight(str, s""", java.nio.charset.Charset.forName("$other")""")
              case CharsetValue.OtherCharset(other) =>
                Patch.addRight(str, s""", ${other}""")
            }
            .asPatch
        ).asPatch
    }.asPatch
  }
}
