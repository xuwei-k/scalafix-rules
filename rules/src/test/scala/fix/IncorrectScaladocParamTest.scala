package fix

import org.scalatest.exceptions.TestPendingException
import org.scalatest.funsuite.AnyFunSuite
import scala.meta.inputs.Input
import scalafix.internal.config.ScalaVersion
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument

class IncorrectScaladocParamTest extends AnyFunSuite {
  test("Scala 2") {
    if (scala.util.Properties.isWin) {
      throw new TestPendingException
    }
    val x = new IncorrectScaladocParam
    val in = SyntacticDocument.fromInput(
      input = Input.String("""
          |/**
          |  * @param x1 aaa
          |  * @param yyy zzz
          |  */
          |abstract class A(x1: Int) {
          |
          | /**
          |   * @param hhh zzz
          |   */
          |  def this(y: S) = { this(3) }
          |
          | /**
          |   * @param y1 bbb
          |   * @param ccc ddd
          |   */
          |  def f1(y1: Int): Int
          |
          | /**
          |  * @param y2 bbb
          |  * @param eee fff
          |  */
          |  def f2(y2: Int): Int = y2
          |
          | /**
          |   * @param y3 12345
          |   * @param ggg hhh
          |   */
          |  def f3(y3: Int) = macro macroImpl
          |}
          |""".stripMargin),
      scalaVersion = ScalaVersion.scala2
    )
    val result = x.fix0(in)
    assert(result.size == 5)
    result.foreach { x =>
      assert(x.message == "incorrect @param")
      assert(x.severity == LintSeverity.Warning)
    }
    assert(result.map(_.position.startLine).sorted == Seq(3, 8, 14, 20, 26))
    val values =
      result.map(x => Input.Slice(input = in.input, start = x.position.start, end = x.position.end).text).toSet
    assert(
      values == Set(
        "  * @param yyy zzz",
        "   * @param hhh zzz",
        "   * @param ccc ddd",
        "  * @param eee fff",
        "   * @param ggg hhh",
      )
    )
  }

  test("Scala 3") {
    if (scala.util.Properties.isWin) {
      throw new TestPendingException
    }
    val x = new IncorrectScaladocParam
    val in = SyntacticDocument.fromInput(
      input = Input.String("""
          |/**
          |  * @param x1 aaa
          |  * @param yyy zzz
          |  */
          |enum A(x1: Int) {
          | /**
          |  * @param x2 aaa
          |  */
          |  case B(x2: Int) extends A(x2)
          |
          | /**
          |  * @param bbb ccc
          |  */
          |  case C(x2: Int) extends A(x2)
          |}
          |""".stripMargin),
      scalaVersion = ScalaVersion.scala3
    )
    val result = x.fix0(in)
    assert(result.size == 2)
    result.foreach { x =>
      assert(x.message == "incorrect @param")
      assert(x.severity == LintSeverity.Warning)
    }
    assert(result.map(_.position.startLine).sorted == Seq(3, 12))
    val values =
      result.map(x => Input.Slice(input = in.input, start = x.position.start, end = x.position.end).text).toSet
    assert(
      values == Set(
        "  * @param yyy zzz",
        "  * @param bbb ccc",
      )
    )
  }
}
