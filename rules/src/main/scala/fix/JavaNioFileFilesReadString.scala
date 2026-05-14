package fix

import fix.JavaNioFileFilesReadString.CharsetValue
import scala.meta._
import scalafix.v1._

object JavaNioFileFilesReadString {
  private sealed abstract class CharsetValue

  private object CharsetValue {

    case object UTF8 extends CharsetValue

    final case class Other(value: String) extends CharsetValue

    def unapply(t: Term): Option[CharsetValue] = PartialFunction.condOpt(t) {
      case Term.Name("UTF_8") | Term.Select(
            Term.Name("StandardCharsets"),
            Term.Name("UTF_8")
          ) | Lit.String("UTF-8") =>
        UTF8
      case Lit.String(other) =>
        Other(other)
    }
  }
}

class JavaNioFileFilesReadString extends SyntacticRule("JavaNioFileFilesReadString") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.New(
            Init.After_4_6_0(
              Type.Name("String") | Type.Select(
                Term.Select(
                  Term.Name("java"),
                  Term.Name("lang")
                ),
                Type.Name("String")
              ),
              Name.Anonymous(),
              List(
                Term.ArgClause(
                  List(
                    x @ Term.Apply.After_4_6_0(
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
                        readAllByes @ Term.Name("readAllBytes")
                      ),
                      Term.ArgClause(
                        path :: Nil,
                        None
                      )
                    ),
                    CharsetValue(c)
                  ),
                  None
                )
              )
            )
          ) =>
        Seq(
          Patch.removeTokens(t.tokens.takeWhile(_.pos.start < x.pos.start)),
          Patch.replaceTree(readAllByes, "readString"),
          Patch.removeTokens(t.tokens.reverse.takeWhile(_.pos.end > x.pos.end)),
          PartialFunction
            .condOpt(c) { case CharsetValue.Other(other) =>
              Patch.addRight(path, s""", java.nio.charset.Charset.forName("$other")""")
            }
            .asPatch
        ).asPatch
    }.asPatch
  }
}
