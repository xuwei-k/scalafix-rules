package fix

import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class KindProjectorScala3TypeLambda extends SyntacticRule("KindProjectorScala3TypeLambda") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Type.Apply.After_4_6_0(
            Type.Name("λ"),
            Type.ArgClause(
              Type.Function.After_4_6_0(
                Type.FuncParamClause(args),
                body
              ) :: Nil
            )
          ) =>
        Patch.replaceTree(t, args.mkString("[", ", ", s"] =>> ${body}"))
    }.asPatch
  }
}
