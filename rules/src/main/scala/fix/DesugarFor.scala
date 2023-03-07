package fix

import scala.meta.Enumerator
import scala.meta.Pat
import scala.meta.Term
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class DesugarForYield extends SyntacticRule("DesugarForYield") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case f: Term.ForYield =>
      val xs :+ x = f.enums.collect { case g @ Enumerator.Generator(Pat.Var(_), _) =>
        g
      }

      if (f.enums.size == (xs.size + 1)) {
        val seed = s"(${x.rhs}).map{ ${x.pat} => ${f.body} }"
        val result = xs.foldRight(seed) { case (z, acc) =>
          s"(${z.rhs}).flatMap{ ${z.pat} => $acc }"
        }
        Patch.replaceTree(f, result)
      } else {
        Patch.empty
      }
    }.asPatch
  }
}
