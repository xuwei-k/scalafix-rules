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
import scala.meta.Lit
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Term
import scala.meta.Type

object CirceCodec {
  private sealed abstract class TypeClass extends Product with Serializable
  private case object Both extends TypeClass
  private case object Encoder extends TypeClass
  private case object Decoder extends TypeClass

  private object TypeClass {
    def unapply(args: List[List[Term]]): Option[CirceCodec.TypeClass] =
      PartialFunction.condOpt(args) {
        case Nil =>
          CirceCodec.Both
        case List(List(Term.Assign(Term.Name("encodeOnly"), Lit.Boolean(true)))) =>
          CirceCodec.Encoder
        case List(List(Term.Assign(Term.Name("decodeOnly"), Lit.Boolean(true)))) =>
          CirceCodec.Decoder
      }
  }
}

class CirceCodec extends SyntacticRule("CirceCodec") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case src: Source =>
      val result = src.collect { case clazz: Defn.Class =>
        clazz.mods.collect {
          case annotation @ Annot(Init(Type.Name("JsonCodec"), _, CirceCodec.TypeClass(t))) =>
            val objectOpt = src.collect {
              case obj: Defn.Object if obj.name.value == clazz.name.value => obj
            }.headOption

            val className = clazz.name.value

            val instance = {
              t match {
                case CirceCodec.Both =>
                  s"implicit val codec: Codec.AsObject[${className}] = deriveCodec[${className}]\n"
                case CirceCodec.Encoder =>
                  s"implicit val encoder: Encoder.AsObject[${className}] = deriveEncoder[${className}]\n"
                case CirceCodec.Decoder =>
                  s"implicit val decoder: Decoder[${className}] = deriveDecoder[${className}]\n"
              }
            }

            List(
              (
                t,
                Seq(
                  Patch.removeTokens(annotation.tokens),
                  objectOpt match {
                    case Some(obj) =>
                      Patch.addLeft(obj.templ.stats.head, instance)
                    case None =>
                      Patch.addRight(clazz, s"\n\nobject ${className} {\n${instance}\n}")
                  }
                ).asPatch
              )
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

        val imports = {
          val types = result.map(_._1).toSet
          List(
            if (types(CirceCodec.Encoder)) {
              List("import io.circe.Encoder", "import io.circe.generic.semiauto.deriveEncoder")
            } else {
              Nil
            },
            if (types(CirceCodec.Decoder)) {
              List("import io.circe.Decoder", "import io.circe.generic.semiauto.deriveDecoder")
            } else {
              Nil
            },
            if (types(CirceCodec.Both)) {
              List("import io.circe.Codec", "import io.circe.generic.semiauto.deriveCodec")
            } else {
              Nil
            },
          ).flatten.sorted.mkString("\n", "\n", "\n")
        }

        Seq(
          annotationImport,
          Patch.addLeft(pkg, imports),
          result.map(_._2).asPatch
        ).asPatch
      } else {
        Patch.empty
      }
    }.asPatch

  }
}
