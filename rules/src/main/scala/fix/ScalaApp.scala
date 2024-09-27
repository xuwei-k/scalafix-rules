package fix

import scala.meta.Defn
import scala.meta.Init
import scala.meta.Pkg
import scala.meta.Template
import scala.meta.Type
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

/**
 * @see [[https://docs.scala-lang.org/scala3/book/methods-main-methods.html]]
 */
class ScalaApp extends SyntacticRule("ScalaApp") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case x1 @ Defn.Object(_, _, Template.Initial(_, (app @ Init.Initial(Type.Name("App"), _, _)) :: tail, _, stats))
          if x1.parent.exists(_.is[Pkg.Body]) || x1.parent.isEmpty =>
        val (classes, newBody) = stats.partition(s => s.is[Defn.Trait] || s.is[Defn.Class] || s.is[Defn.Object])

        // TODO
        // - exclude override def, val
        // - "final val" to "val"

        Seq(
          {
            if (tail.isEmpty) {
              x1.tokens.find(_.is[Token.KwExtends])
            } else {
              x1.tokens.find(_.is[Token.KwWith])
            }
          }.map(e => Patch.removeToken(e)).toList.asPatch,
          Patch.removeTokens(classes.flatMap(_.tokens)),
          Patch.removeTokens(app.tokens),
          Patch.addLeft(newBody.head, "def main(args: Array[String]): Unit = {\n  "),
          Patch.addRight(newBody.last, "\n  }"), {
            if (classes.nonEmpty) {
              Patch.addLeft(x1.tokens.last, classes.map("  " + _.toString).mkString("\n", "\n", "\n"))
            } else {
              Patch.empty
            }
          }
        ).asPatch
    }.asPatch
  }
}
