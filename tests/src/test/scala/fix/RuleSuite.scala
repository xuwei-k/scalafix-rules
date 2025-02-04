package fix

import scalafix.testkit.AbstractSemanticRuleSuite
import org.scalatest.funsuite.AnyFunSuiteLike
import scala.util.Properties

class RuleSuite extends AbstractSemanticRuleSuite with AnyFunSuiteLike {
  private[this] val excludeWindows: Set[String] = Set(
    "FlatMapCollectTest",
    "FileNameConsistentTest",
    "FileNameConsistentTest2",
    "package",
    "DuplicateWildcardImportTest",
    "PartialFunctionCondOptTest",
    "RemoveUselessParamCommentsTest",
    "UnnecessarySortTest",
  ).map(_ + ".scala")

  testsToRun
    .filter(x =>
      if (Properties.isWin) {
        !excludeWindows(x.path.input.toFile.getName)
      } else {
        true
      }
    )
    .foreach(runOn)
}
