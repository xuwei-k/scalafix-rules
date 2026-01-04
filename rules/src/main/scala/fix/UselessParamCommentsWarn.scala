package fix

import java.util.Locale
import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Stat
import scala.meta.Tree
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scala.meta.contrib.AssociatedComments
import scala.meta.inputs.Position
import scala.meta.internal.Scaladoc
import scala.meta.internal.Scaladoc.TagType
import scala.meta.internal.parsers.ScaladocParser
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionOptionPatch
import scalafix.v1.XtensionSeqPatch

final case class UselessParamCommentsWarnConfig(
  message: String
)

object UselessParamCommentsWarnConfig {
  val default: UselessParamCommentsWarnConfig = UselessParamCommentsWarnConfig(
    message = "useless @param"
  )

  implicit val surface: Surface[UselessParamCommentsWarnConfig] =
    metaconfig.generic.deriveSurface[UselessParamCommentsWarnConfig]

  implicit val decoder: ConfDecoder[UselessParamCommentsWarnConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class UselessParamCommentsWarn(config: UselessParamCommentsWarnConfig)
    extends SyntacticRule("UselessParamCommentsWarn") {

  def this() = this(UselessParamCommentsWarnConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("UselessParamCommentsWarn")(this.config)
      .map(newConfig => new UselessParamCommentsWarn(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t: Stat.WithCtor =>
        p(t, doc.comments)
      case t: Tree.WithParamClauseGroups =>
        p(t, doc.comments)
    }.asPatch
  }

  private def p(t: Tree, comments: AssociatedComments)(implicit doc: SyntacticDocument): Patch = {
    comments
      .leading(t)
      .flatMap(x =>
        ScaladocParser
          .parse(x.syntax)
          .toSeq
          .flatMap(_.para.flatMap(_.terms))
          .collect { case c @ Scaladoc.Tag(TagType.Param, _, _) =>
            (
              x,
              c.label.map(_.value.toLowerCase(Locale.ROOT)),
              c.desc.collect { case text: Scaladoc.Text =>
                text.parts.map(_.part.syntax.toLowerCase(Locale.ROOT))
              }.flatten
            )
          }
          .collect {
            case (y, Some(x1), Seq(x2)) if x1 == x2 =>
              PartialFunction
                .condOpt(y.value.linesIterator.zipWithIndex.collect {
                  case (str, i) if str.contains(s" ${x1} ") => (str.length + 1, i)
                }.toList) { case List((length, index)) =>
                  Patch.lint(
                    Diagnostic(
                      id = "",
                      message = config.message,
                      position = {
                        val line = y.pos.startLine + index
                        Position.Range(
                          input = doc.input,
                          startLine = line,
                          startColumn = 0,
                          endLine = line,
                          endColumn = length
                        )
                      },
                      severity = LintSeverity.Warning
                    )
                  )
                }
                .asPatch
          }
      )
      .asPatch
  }
}
