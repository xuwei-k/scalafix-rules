package fix

import scala.meta.Defn
import scala.meta.Mod
import scala.meta.Pat
import scala.meta.Term
import scala.meta.Type
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

object ObjectFinal {
  private val constantTypes: Seq[Type] = {
    val values = Seq(
      "Boolean",
      "Byte",
      "Short",
      "Char",
      "Int",
      "Long",
      "Float",
      "Double",
    ).map(Type.Name.apply)

    Seq(
      values.flatMap { x =>
        Seq(
          x,
          Type.Select(
            Term.Name("scala"),
            x,
          ),
          Type.Select(
            Term.Select(
              Term.Name("_root_"),
              Term.Name("scala")
            ),
            x
          )
        )
      }, {
        val str = Type.Name("String")

        Seq(
          str,
          Type.Select(
            Term.Name("Predef"),
            str,
          ),
          Type.Select(
            Term.Select(
              Term.Name("scala"),
              Term.Name("Predef")
            ),
            str
          ),
          Type.Select(
            Term.Select(
              Term.Select(
                Term.Name("_root_"),
                Term.Name("scala")
              ),
              Term.Name("Predef")
            ),
            str
          ),
          Type.Select(
            Term.Select(
              Term.Name("java"),
              Term.Name("lang")
            ),
            str
          ),
          Type.Select(
            Term.Select(
              Term.Select(
                Term.Name("_root_"),
                Term.Name("java")
              ),
              Term.Name("lang")
            ),
            str
          ),
        )
      }
    ).flatten
  }

  private object FinalMod {
    def unapply(values: List[Mod]): Option[Mod] = values.find(_.is[Mod.Final])
  }
}

class ObjectFinal extends SyntacticRule("ObjectFinal") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case o: Defn.Object =>
      o.templ.stats.collect {
        case Defn.Val(
              ObjectFinal.FinalMod(m),
              List(Pat.Var(_: Term.Name)),
              Some(tpe),
              _
            ) if ObjectFinal.constantTypes.forall(_.structure != tpe.structure) =>
          Patch.lint(
            Diagnostic(
              id = "",
              message =
                "redundant `final` for val if not constant https://scala-lang.org/files/archive/spec/2.13/06-expressions.html#constant-expressions",
              position = m.pos,
              severity = LintSeverity.Warning
            )
          )
        case Defn.Def.After_4_7_3(
              ObjectFinal.FinalMod(m),
              _,
              _,
              _,
              _
            ) =>
          Patch.lint(
            Diagnostic(
              id = "",
              message = "redundant final",
              position = m.pos,
              severity = LintSeverity.Warning
            )
          )
      }.asPatch
    }.asPatch
  }
}
