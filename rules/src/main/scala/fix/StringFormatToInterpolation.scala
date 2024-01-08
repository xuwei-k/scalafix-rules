package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Lit
import scala.meta.Term

private object StringFormatToInterpolation {
  private object Extract {
    def unapply(t: Term): Option[(Boolean, String, List[Term])] = PartialFunction
      .condOpt(t) {
        case Term.ApplyInfix.Initial(
              s: Lit.String,
              Term.Name("format"),
              Nil,
              args
            ) =>
          (s, args)
        case Term.Apply.Initial(
              Term.Select(
                s: Lit.String,
                Term.Name("format")
              ),
              args
            ) =>
          (s, args)
      }
      .collect {
        case (s, args)
            if args.nonEmpty &&
              args.lengthCompare(s.value.sliding(2).count(_ == "%s")) == 0 &&
              s.value.forall(_ != '\\') && // TODO support backslash
              (s.value.forall(_ != '"') || s.syntax.startsWith("\"\"\"")) && // TODO support double-quote
              !args.exists(_.is[Term.Placeholder]) =>
          (s.syntax.startsWith("\"\"\""), s.value, args)
      }
  }
}

class StringFormatToInterpolation extends SyntacticRule("StringFormatToInterpolation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ StringFormatToInterpolation.Extract(triple, str, args) =>
      val buf =
        if (triple) {
          new java.lang.StringBuilder("s\"\"\"")
        } else {
          new java.lang.StringBuilder("s\"")
        }
      str.split("%s", -1).zipWithIndex.foreach { case (s, i) =>
        buf.append(s.replace("\n", "\\n"))
        if (i < args.length) {
          buf.append("${")
          buf.append(args(i))
          buf.append("}")
        }
      }
      if (triple) {
        buf.append("\"\"\"")
      } else {
        buf.append("\"")
      }
      Patch.replaceTree(t, buf.toString)
    }.asPatch
  }
}
