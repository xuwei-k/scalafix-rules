package fix

import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1.*

class PromiseFailure extends SemanticRule("PromiseFailure") {
  private def isPromise(t: Tree)(implicit doc: SemanticDocument): Boolean = {
    t.symbol.info
      .map(_.signature)
      .collect {
        case ValueSignature(tpe: TypeRef) if tpe.symbol.value == "scala/concurrent/Promise#" =>
      }
      .isDefined
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Defn.Val(
            _,
            List(a: Pat.Var),
            _,
            _
          ) if isPromise(a.name) =>
        // ここでdoc.treeで、以下の実装ではファイル内部全部を雑に探索しているが、
        // 同じ変数名があるとバグるので、
        // 場合によって探索範囲を絞るなどの工夫が必要
        val callFailure = doc.tree.collect {
          case Term.Apply.After_4_6_0(
                Term.Select(x: Term.Name, Term.Name("failure")),
                Term.ArgClause(List(_), None)
              ) if x.value == a.name.value && isPromise(x) =>
          case Term.ApplyInfix.After_4_6_0(
                x: Term.Name,
                Term.Name("failure"),
                Type.ArgClause(Nil),
                Term.ArgClause(List(_), None)
              ) if x.value == a.name.value && isPromise(x) =>
        }.nonEmpty

        if (callFailure) {
          // failure呼び忘れてないのでOK
          Patch.empty
        } else {
          Patch.lint(
            Diagnostic(
              id = "",
              message = "このPromiseに対してfailureメソッド呼び忘れてませんか？？？",
              position = a.pos,
              severity = LintSeverity.Warning
            )
          )
        }
    }.asPatch
  }
}
