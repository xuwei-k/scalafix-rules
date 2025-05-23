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
          |
          | /**
          |   * @param aaa bbb
          |   * @param aaa ccc
          |   */
          |  def f4(aaa: Int): Int
          |}
          |""".stripMargin),
      scalaVersion = ScalaVersion.scala2
    )
    val result = x.fix0(in)
    assert(result.size == 7)
    result.foreach { x =>
      assert(x.severity == LintSeverity.Warning)
    }
    assert(result.count(_.message == "incorrect @param") == 5)
    assert(result.count(_.message == "duplicate @param aaa") == 2)
    assert(result.map(_.position.startLine).sorted == Seq(3, 8, 14, 20, 26, 31, 32))
    val values =
      result.map(x => Input.Slice(input = in.input, start = x.position.start, end = x.position.end).text).toSet
    assert(
      values == Set(
        "  * @param yyy zzz",
        "   * @param hhh zzz",
        "   * @param ccc ddd",
        "  * @param eee fff",
        "   * @param ggg hhh",
        "   * @param aaa bbb",
        "   * @param aaa ccc",
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
          |
          |/**
          | * @param a aaa
          | * @param b bbb
          | */
          |def interleaved[A](a: A)[B](b: B): (A, B) = (a, b)
          |
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
