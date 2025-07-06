package fix

import java.util.Locale
import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Defn
import scala.meta.XtensionCollectionLikeUI
import scala.meta.contrib.DocToken
import scala.meta.contrib.XtensionCommentOps
import scala.meta.inputs.Position
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
    doc.tree.collect { case t: Defn.Class =>
      doc.comments
        .leading(t)
        .flatMap(x => x.docTokens.toList.flatten.map(x -> _))
        .map { case (x, c) =>
          PartialFunction
            .condOpt(
              (c.kind, c.name.map(_.toLowerCase(Locale.ROOT)), c.body.map(_.toLowerCase(Locale.ROOT)))
            ) {
              case (DocToken.Param, Some(x1), Some(x2)) if x1 == x2 =>
                PartialFunction
                  .condOpt(x.value.linesIterator.zipWithIndex.collect {
                    case (str, i) if str.contains(s" ${x1} ") => (str.length + 1, i)
                  }.toList) { case List((length, index)) =>
                    Patch.lint(
                      Diagnostic(
                        id = "",
                        message = config.message,
                        position = {
                          val line = x.pos.startLine + index
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
            .asPatch
        }
        .asPatch
    }.asPatch
  }
}
