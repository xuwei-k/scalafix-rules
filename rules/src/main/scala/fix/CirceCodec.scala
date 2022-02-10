package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Mod.Annot
import scala.meta.Name.Indeterminate
import scala.meta.Term.Select
import scala.meta.Defn
import scala.meta.Import
import scala.meta.Importee
import scala.meta.Importer
import scala.meta.Init
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Term
import scala.meta.Type

class CirceCodec extends SyntacticRule("CirceCodec") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case src: Source =>
      val result = src.collect { case clazz: Defn.Class =>
        clazz.mods.collect {
          case annotation @ Annot(Init(Type.Name("JsonCodec"), _, Nil)) =>
            val objectOpt = src.collect {
              case obj: Defn.Object if obj.name.value == clazz.name.value => obj
            }.headOption

            val className = clazz.name.value
            val codec = s"implicit val codec: Codec.AsObject[${className}] = deriveCodec[${className}]\n"

            Seq(
              Patch.removeTokens(annotation.tokens),
              objectOpt match {
                case Some(obj) =>
                  Patch.addLeft(obj.templ.stats.head, codec)
                case None =>
                  Patch.addRight(clazz, s"\n\nobject ${className} {\n${codec}\n}")
              }
            )
          case _ =>
            Nil
        }.flatten
      }.flatten

      if (result.nonEmpty) {
        val pkg = src.collect { case p: Pkg => p.stats.head }.head

        val annotationImport = src.collect {
          case i @ Import(
                List(
                  Importer(
                    Select(Select(Term.Name("io"), Term.Name("circe")), Term.Name("generic")),
                    List(Importee.Name(Indeterminate("JsonCodec")))
                  )
                )
              ) =>
            Patch.removeTokens(i.tokens)
        }.asPatch

        Seq(
          annotationImport,
          Patch.addLeft(pkg, "\nimport io.circe.Codec\nimport io.circe.generic.semiauto.deriveCodec\n"),
          result.asPatch
        ).asPatch
      } else {
        Patch.empty
      }
    }.asPatch

  }
}
