package fix

import scala.meta._
import scalafix.v1._

class ScalazEitherInfix extends SemanticRule("ScalazEitherInfix") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case x: Type.Apply if """scalaz/`\/`#""" == x.tpe.symbol.value && x.args.size == 2 =>
        Patch.replaceTree(x, s"""${x.args(0)} \\/ ${x.args(1)}""")
    }.asPatch
  }
}
