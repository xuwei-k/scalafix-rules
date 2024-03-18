package fix

import fix.SizeIs.MaybeScalaCollection
import scala.meta._
import scalafix.Patch
import scalafix.v1._

private object SizeIs {
  private object MaybeScalaCollection {
    def unapply(sym: Symbol): Boolean =
      sym.normalized.value.startsWith("scala.collection.")
  }
}

class SizeIs extends SemanticRule("SizeIs") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.ApplyInfix.After_4_6_0(
            Term.Select(x1, sizeOrLength @ Term.Name("length" | "size")),
            Term.Name("<" | "<=" | "==" | "!=" | ">" | ">="),
            Type.ArgClause(Nil),
            Term.ArgClause(_ :: Nil, None)
          ) =>
        def p: Patch = Patch.replaceTree(sizeOrLength, s"${sizeOrLength.value}Is")
        x1.symbol.info
          .map(_.signature)
          .collect {
            case ValueSignature(TypeRef(_, MaybeScalaCollection(), _ :: _ :: Nil)) =>
              p
            case ValueSignature(TypeRef(_, MaybeScalaCollection(), _ :: Nil)) =>
              p
          }
          .asPatch
    }.asPatch
  }
}
