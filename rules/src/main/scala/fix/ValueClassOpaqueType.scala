package fix

import scala.meta._
import scalafix.v1._

class ValueClassOpaqueType extends SyntacticRule("ValueClassOpaqueType") {
  val exclude: Set[String] = Set(
  )
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Class.After_4_6_0(
            List(
              Mod.Case()
            ),
            Type.Name(className),
            Type.ParamClause(Nil),
            Ctor.Primary.After_4_6_0(
              Nil,
              Name.Anonymous(),
              List(
                Term.ParamClause(
                  List(
                    Term.Param(
                      Nil,
                      fieldName: Term.Name,
                      Some(
                        tpe: Type
                      ),
                      None
                    )
                  ),
                  None
                )
              )
            ),
            Template.After_4_9_9(
              None,
              List(
                Init.After_4_6_0(
                  Type.Name("AnyVal"),
                  Name.Anonymous(),
                  Nil
                )
              ),
              Template.Body(
                None,
                Nil
              ),
              Nil
            )
          ) if !exclude(className) && doc.tree.collect {
            case o: Defn.Object if o.name.value == className => ()
          }.isEmpty =>
        Patch.replaceTree(
          t,
          List(
            s"opaque type ${className} = ${tpe}",
            s"",
            s"object ${className}{",
            s"  def apply(${fieldName}: ${tpe}): ${className} = ${fieldName}",
            s"",
            s"  extension (x: ${className}) {",
            s"    def ${fieldName}: ${tpe} = x",
            s"  }",
            s"}",
            s""
          ).mkString("\n")
        )
    }.asPatch
  }
}
