package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Lit
import scala.meta.Term

class StringFormatToInterpolation extends SyntacticRule("StringFormatToInterpolation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply(
            Term.Select(
              Lit.String(str),
              Term.Name("format")
            ),
            args
          ) if args.nonEmpty && args.lengthCompare(str.sliding(2).count(_ == "%s")) == 0 =>
        if (str == "%s") {
          Patch.replaceTree(t, "s\"${" + args.head + "}\"")
        } else {
          val buf = new java.lang.StringBuilder("s\"")
          str.split("%s").zipWithIndex.foreach { case (s, i) =>
            buf.append(s)
            if (i < args.length) {
              buf.append("${")
              buf.append(args(i))
              buf.append("}")
            }
          }
          buf.append("\"")
          Patch.replaceTree(t, buf.toString)
        }
    }.asPatch
  }
}
