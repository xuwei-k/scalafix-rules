package fix

import fix.ExplicitResultTypesWarnConfig.MemberKind
import fix.ExplicitResultTypesWarnConfig.MemberVisibility
import scala.meta._
import scalafix.lint.LintSeverity
import scalafix.v1._

object ExplicitResultTypesWarnConfig {

  sealed abstract class MemberVisibility(val value: String)

  object MemberVisibility {
    case object Public extends MemberVisibility("public")
    case object Protected extends MemberVisibility("protected")
    case object Private extends MemberVisibility("private")

    val all: List[MemberVisibility] =
      List(Public, Protected, Private)
  }

  sealed trait MemberKind

  object MemberKind {
    case object Def extends MemberKind
    case object Val extends MemberKind
    case object Var extends MemberKind

    val all: List[MemberKind] =
      List(Def, Val, Var)
  }

  val default: ExplicitResultTypesWarnConfig = ExplicitResultTypesWarnConfig()
}

case class ExplicitResultTypesWarnConfig(
  memberKind: List[MemberKind] = List(MemberKind.Def, MemberKind.Val, MemberKind.Var),
  memberVisibility: List[MemberVisibility] = List(MemberVisibility.Public),
)

class ExplicitResultTypesWarn(config: ExplicitResultTypesWarnConfig) extends SyntacticRule("ExplicitResultTypesWarn") {
  def this() = this(ExplicitResultTypesWarnConfig.default)

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case x: Stat.WithTemplate if x.parent.forall(_.is[Pkg.Body]) =>
        x.templ.body.stats.collect {
          case t @ Defn.Val(_, Pat.Var(n) :: Nil, None, _) if isRuleCandidate(t, t.mods, t.rhs) =>
            n -> visibility(t.mods)
          case t @ Defn.Var.After_4_7_2(_, Pat.Var(n) :: Nil, None, _) if isRuleCandidate(t, t.mods, t.body) =>
            n -> visibility(t.mods)
          case t: Defn.Def if t.decltpe.isEmpty && isRuleCandidate(t, t.mods, t.body) =>
            t.name -> visibility(t.mods)
        }
    }.flatten.map { case (t, v) =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = s"please add explicit type for ${v.value} member or change visibility",
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    }.asPatch
  }

  private def visibility(mods: Iterable[Mod]): MemberVisibility =
    mods.collectFirst {
      case _: Mod.Private => MemberVisibility.Private
      case _: Mod.Protected => MemberVisibility.Protected
    }.getOrElse(MemberVisibility.Public)

  private def kind(defn: Defn): Option[MemberKind] = PartialFunction.condOpt(defn) {
    case _: Defn.Val => MemberKind.Val
    case _: Defn.Def => MemberKind.Def
    case _: Defn.Var => MemberKind.Var
  }

  private def isRuleCandidate(
    defn: Defn,
    mods: Iterable[Mod],
    body: Term
  ): Boolean = {
    !body.is[Lit] && kind(defn).exists(config.memberKind.contains) && config.memberVisibility.contains(visibility(mods))
  }
}
