package fix

import metaconfig.ConfDecoder
import metaconfig.ConfError
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

case class UnnecessarySortRewriteConfig(rewriteConfig: UnnecessarySortRewriteConfig.RewriteConfig)

object UnnecessarySortRewriteConfig {
  sealed abstract class RewriteConfig(val value: String) extends Product with Serializable
  object RewriteConfig {
    val map: Map[String, RewriteConfig] = Seq(Only212Methods, AddCompatImport, Default).map(a => a.value -> a).toMap

    implicit val decoder: ConfDecoder[RewriteConfig] =
      implicitly[ConfDecoder[String]].flatMap { str =>
        Configured.opt(map.get(str))(ConfError.message(s"invalid type ${str}"))
      }
  }
  case object Only212Methods extends RewriteConfig("only212methods")
  case object AddCompatImport extends RewriteConfig("addCompatImport")
  case object Default extends RewriteConfig("default")

  val default: UnnecessarySortRewriteConfig = UnnecessarySortRewriteConfig(rewriteConfig = Default)

  implicit val surface: Surface[UnnecessarySortRewriteConfig] =
    metaconfig.generic.deriveSurface[UnnecessarySortRewriteConfig]

  implicit val decoder: ConfDecoder[UnnecessarySortRewriteConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class UnnecessarySortRewrite(config: UnnecessarySortRewriteConfig) extends SyntacticRule("UnnecessarySortRewrite") {

  def this() = this(UnnecessarySortRewriteConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("UnnecessarySortRewrite")(this.config).map(newConfig => new UnnecessarySortRewrite(newConfig))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case src: Source =>
      val result = src.collect {
        case t @ Term.Select(
              Term.Apply.After_4_6_0(
                Term.Select(x1, Term.Name("sortBy")),
                Term.ArgClause(x2 :: Nil, tp)
              ),
              Term.Name(methodName)
            ) if UnnecessarySort.map.contains(methodName) =>
          val patch1 = Patch.replaceTree(
            t,
            Term.Apply
              .After_4_6_0(
                Term.Select(x1, Term.Name(UnnecessarySort.map(methodName))),
                Term.ArgClause(x2 :: Nil, tp)
              )
              .toString,
          )

          config.rewriteConfig match {
            case UnnecessarySortRewriteConfig.Only212Methods =>
              if (!UnnecessarySort.scala213Methods.contains(methodName)) {
                Option((patch1, false))
              } else {
                None
              }
            case UnnecessarySortRewriteConfig.AddCompatImport =>
              if (UnnecessarySort.scala213Methods.contains(methodName)) {
                Option((patch1, true))
              } else {
                Option((patch1, false))
              }
            case UnnecessarySortRewriteConfig.Default =>
              Option((patch1, false))
          }
      }.flatten

      if (result.nonEmpty) {
        val patch = result.map(_._1).asPatch
        val pkg = src.collect { case p: Pkg => p.stats.head }.head
        if (result.exists(_._2)) {
          Seq(
            Patch.addLeft(pkg, "\nimport scala.collection.compat._\n"),
            patch,
          ).asPatch
        } else {
          patch
        }
      } else {
        Patch.empty
      }
    }.asPatch
  }
}
