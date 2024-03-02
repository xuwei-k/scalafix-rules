package fix

import scala.meta.Type
import scala.meta.transversers._
import scalafix.Patch
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

class ScalazEitherInfix extends SemanticRule("ScalazEitherInfix") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case x @ Type.Apply.After_4_6_0(_, Type.ArgClause(t1 :: t2 :: Nil)) if """scalaz/`\/`#""" == x.tpe.symbol.value =>
        Patch.replaceTree(x, s"""${t1} \\/ ${t2}""")
    }.asPatch
  }
}
