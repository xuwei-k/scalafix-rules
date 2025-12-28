package fix

import scala.meta.Defn
import scala.meta.Pat
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.TypeRef
import scalafix.v1.ValueSignature
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

private object UnnecessaryPartitionMap {
  private val ScalaCollection: Set[String] = Set(
    "scala/package.Seq#",
    "scala/package.Range#",
    "scala/package.List#",
    "scala/package.Vector#",
    "scala/Predef.Set#",
    "scala/Predef.Map#",
    "scala/collection/immutable/Set#",
    "scala/collection/immutable/Map#",
    "scala/collection/immutable/Seq#",
    "scala/collection/immutable/List#",
    "scala/collection/immutable/Vector#",
    "scala/collection/immutable/LazyList#",
    "scala/collection/mutable/Seq#",
    "scala/collection/mutable/Set#",
    "scala/collection/mutable/Map#",
    "scala/collection/Seq#",
    "scala/collection/Set#",
    "scala/collection/Map#",
    "scala/Array#",
  )
}

class UnnecessaryPartitionMap extends SemanticRule("UnnecessaryPartitionMap") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Defn.Val(
            _,
            List(
              Pat.Tuple(
                List(
                  Pat.Var(_: Term.Name),
                  Pat.Wildcard()
                ) | List(
                  Pat.Wildcard(),
                  Pat.Var(_: Term.Name),
                )
              )
            ),
            None,
            Term.Apply.After_4_6_0(
              Term.Select(
                obj,
                partitionMap @ Term.Name("partitionMap")
              ),
              Term.ArgClause(
                _ :: Nil,
                None
              )
            )
          ) =>
        obj.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(tpe: TypeRef) if UnnecessaryPartitionMap.ScalaCollection(tpe.symbol.value) =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = "unnecessary partitionMap",
                  position = partitionMap.pos,
                  severity = LintSeverity.Warning
                )
              )
          }
          .asPatch
    }.asPatch
  }
}
