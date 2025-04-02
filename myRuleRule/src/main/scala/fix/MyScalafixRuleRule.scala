package fix

import scala.meta.Defn
import scala.meta.Init
import scala.meta.Lit
import scala.meta.Mod
import scala.meta.Name
import scala.meta.Pat
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

  override def fix(implicit doc: SyntacticDocument): Patch = Seq(
    if (
      doc.tree.collect {
        case Defn.Def.After_4_7_3(
              List(
                Mod.Protected(Name.Anonymous())
              ),
              Term.Name("severity"),
              Nil,
              Some(
                Type.Name("LintSeverity")
              ),
              Term.Select(
                Term.Name("LintSeverity"),
                Term.Name("Warning")
              )
            ) =>
          ()
      }.nonEmpty && doc.tree.collect {
        case t: Defn.Class if t.name.value.endsWith("Error") =>
      }.isEmpty
    ) {
      Patch.lint(
        Diagnostic(
          id = "",
          message = "not found Error rule",
          position = doc.tree.pos,
          severity = LintSeverity.Error
        )
      )
    } else {
      Patch.empty
    },
    doc.tree.collect {
      case t: Defn.Object if t.name.value.endsWith("Error") =>
        val warnSuffix = "Warn"
        Seq(
          t.templ.inits.collect {
            case Init.After_4_6_0(
                  Type.Name(c),
                  _,
                  Nil
                )
                if (s"${c}Error" != t.name.value) && (c
                  .endsWith(warnSuffix) && s"${c.dropRight(warnSuffix.length)}Error" != t.name.value) =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = "invalid class name",
                  position = t.pos,
                  severity = LintSeverity.Error
                )
              )
          }.asPatch,
          t.templ.body match {
            case Template.Body(
                  None,
                  List(
                    Defn.Val(
                      List(
                        Mod.Override()
                      ),
                      List(
                        Pat.Var(Term.Name("name"))
                      ),
                      Some(
                        Type.Name("RuleName")
                      ),
                      Term.Apply.After_4_6_0(
                        Term.Name("RuleName"),
                        Term.ArgClause(
                          List(
                            Term.Select(
                              Term.Select(
                                Term.This(Name.Anonymous()),
                                Term.Name("getClass")
                              ),
                              Term.Name("getSimpleName")
                            )
                          ),
                          None
                        )
                      )
                    ),
                    Defn.Def.After_4_7_3(
                      List(
                        Mod.Override(),
                        Mod.Protected(Name.Anonymous())
                      ),
                      Term.Name("severity"),
                      Nil,
                      Some(
                        Type.Name("LintSeverity")
                      ),
                      Term.Select(
                        Term.Name("LintSeverity"),
                        Term.Name("Error")
                      )
                    )
                  )
                ) =>
              Patch.empty
            case _ =>
              Patch.lint(
                Diagnostic(
                  id = "",
                  message = "invalid Error rule",
                  position = t.pos,
                  severity = LintSeverity.Error
                )
              )
          }
        ).asPatch
      case t: Defn.Def if t.name.value == "fix" =>
        t.collect {
          case x @ Term.Select(
                Term.Name("LintSeverity"),
                _
              ) =>
            Patch.lint(
              Diagnostic(
                id = "",
                message = "don't use LintSeverity directory",
                position = x.pos,
                severity = LintSeverity.Error
              )
            )
        }.asPatch
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
  ).asPatch
}
