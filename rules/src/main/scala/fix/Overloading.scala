package fix

import scala.meta.Decl
import scala.meta.Defn
import scala.meta.Position
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class Overloading extends SyntacticRule("Overloading") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Defn.Trait =>
        t.templ
      case t: Defn.Class =>
        t.templ
      case t: Defn.Object =>
        t.templ
    }.map(_.stats)
      .map { s =>
        val overloadMethods = s.collect {
          case a: Defn.Def =>
            a.name -> Right(a)
          case a: Decl.Def =>
            a.name -> Left(a)
        }.groupBy(_._1.value).values.map(_.map(_._2)).filter(_.size > 1)

        overloadMethods.flatMap {
          _.map {
            case Right(x) =>
              Patch.lint(OverloadWarn(x.pos))
            case Left(x) =>
              Patch.lint(OverloadWarn(x.pos))
          }
        }.asPatch
      }
      .asPatch
  }
}

case class OverloadWarn(override val position: Position) extends Diagnostic {
  override def message = "Don't use overload"
  override def severity = LintSeverity.Warning
}
