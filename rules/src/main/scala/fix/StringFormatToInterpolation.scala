package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Lit
import scala.meta.Term

private object StringFormatToInterpolation {
  private object Extract {
    def unapply(t: Term): Option[(String, List[Term])] = PartialFunction
      .condOpt(t) {
        case Term.ApplyInfix(
              Lit.String(str),
              Term.Name("format"),
              Nil,
              args
            ) =>
          (str, args)
        case Term.Apply(
              Term.Select(
                Lit.String(str),
                Term.Name("format")
              ),
              args
            ) =>
          (str, args)
      }
      .filter { case (str, args) =>
        args.nonEmpty && args.lengthCompare(str.sliding(2).count(_ == "%s")) == 0
      }
  }
}

class StringFormatToInterpolation extends SyntacticRule("StringFormatToInterpolation") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t @ StringFormatToInterpolation.Extract(str, args) =>
      val buf = new java.lang.StringBuilder("s\"")
      str.split("%s", -1).zipWithIndex.foreach { case (s, i) =>
        buf.append(s)
        if (i < args.length) {
          buf.append("${")
          buf.append(args(i))
          buf.append("}")
        }
      }
      buf.append("\"")
      Patch.replaceTree(t, buf.toString)
    }.asPatch
  }
}
