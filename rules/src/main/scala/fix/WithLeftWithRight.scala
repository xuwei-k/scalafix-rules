package fix

import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

class WithLeftWithRight extends SyntacticRule("WithLeftWithRight") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply.After_4_6_0(
            Term.ApplyType.After_4_6_0(
              Term.Name("Right"),
              Type.ArgClause(left :: _ :: Nil)
            ),
            Term.ArgClause(value :: Nil, None)
          ) =>
        Patch.replaceTree(t, s"Right(${value}).withLeft[$left]")
      case t @ Term.Apply.After_4_6_0(
            Term.ApplyType.After_4_6_0(
              Term.Name("Left"),
              Type.ArgClause(_ :: right :: Nil)
            ),
            Term.ArgClause(value :: Nil, None)
          ) =>
        Patch.replaceTree(t, s"Left(${value}).withRight[$right]")
    }.asPatch
  }
}
