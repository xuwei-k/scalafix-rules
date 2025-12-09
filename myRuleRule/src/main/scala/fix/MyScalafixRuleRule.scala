package fix

import scala.meta.Defn
import scala.meta.Importee
import scala.meta.Init
import scala.meta.Lit
import scala.meta.Mod
import scala.meta.Template
import scala.meta.Term
import scala.meta.Type
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class MyScalafixRuleRule extends SyntacticRule("MyScalafixRuleRule") {
  private[this] def when(cond: Boolean)(patch: => Patch): Patch = {
    if (cond) {
      patch
    } else {
      Patch.empty
    }
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Importee.Wildcard() if t.toString() == "*" =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = "Don't use new wildcard import syntax",
            position = t.pos,
            severity = LintSeverity.Error
          )
        )
      case Defn.Class.After_4_6_0(
            _,
            className,
            _,
            primaryCtor,
            Template.After_4_4_0(
              _,
              List(
                Init.After_4_6_0(
                  Type.Name("SyntacticRule" | "SemanticRule"),
                  _,
                  List(Term.ArgClause(Lit.String(ruleName) :: Nil, _))
                )
              ),
              _,
              stats,
              _
            )
          ) =>
        val withConfigurationMethodOpt =
          stats.collectFirst {
            case d: Defn.Def if d.name.value == "withConfiguration" && d.mods.exists(_.is[Mod.Override]) =>
              d
          }
        Seq(
          when(className.value != ruleName) {
            Patch.lint(
              Diagnostic(
                id = "",
                message = s"${className} != ${ruleName}",
                position = className.pos,
                severity = LintSeverity.Error
              )
            )
          },
          when(primaryCtor.paramClauses.nonEmpty) {
            withConfigurationMethodOpt match {
              case Some(withConfigurationMethod) =>
                when(
                  withConfigurationMethod.body.collect {
                    case Lit.String(x) if x == ruleName => ()
                  }.isEmpty
                ) {
                  Patch.lint(
                    Diagnostic(
                      id = "",
                      message = "maybe incorrect `withConfiguration` method",
                      position = className.pos,
                      severity = LintSeverity.Error
                    )
                  )
                }
              case None =>
                Patch.lint(
                  Diagnostic(
                    id = "",
                    message = "there is primary constructor args but not defined `withConfiguration` method",
                    position = className.pos,
                    severity = LintSeverity.Error
                  )
                )
            }
          }
        ).asPatch
    }.asPatch
  }
}
