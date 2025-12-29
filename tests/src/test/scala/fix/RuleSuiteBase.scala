package fix

import org.scalatest.exceptions.TestPendingException
import org.scalatest.funsuite.AnyFunSuiteLike
import scala.util.Properties
import scalafix.testkit.AbstractSemanticRuleSuite
import scalafix.testkit.RuleTest

abstract class RuleSuiteBase(inputName: String) extends AbstractSemanticRuleSuite with AnyFunSuiteLike {
  private[this] val excludeWindows: Set[String] = Set(
    "FlatMapCollectTest",
    "FileNameConsistentTest",
    "FileNameConsistentTest2",
    "package",
    "DuplicateWildcardImportTest",
    "PartialFunctionCondOptTest",
    "RemoveUselessParamCommentsTest",
    "ScaladocEmptyParamTest",
    "ScaladocEmptyReturnTest",
    "UnnecessarySortTest",
  ).map(_ + ".scala")

  private val myTest: RuleTest = {
    testsToRun.filter(_.path.input.toFile.getName == inputName) match {
      case Seq(t) =>
        t
      case Nil =>
        sys.error(s"not found ${inputName}")
      case other =>
        sys.error(s"duplicate test ${inputName} ${other}")
    }
  }

  if (Properties.isWin && excludeWindows(myTest.path.input.toFile.getName)) {
    test(myTest.path.input.toFile.getName) {
      throw new TestPendingException
    }
  } else {
    runOn(myTest)
  }
}
