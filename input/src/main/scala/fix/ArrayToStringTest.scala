/*
rule = ArrayToString
 */
package fix

object ArrayToStringTest {
  def int(a: Array[Int]): String = a.toString // assert: ArrayToString
  def any(a: Array[Any]): String = a.toString // assert: ArrayToString
  def generic[A](a: Array[A]): String = a.toString // assert: ArrayToString
  def list(a: List[Int]): String = a.toString

  def f1: Seq[String] = Seq(
    Array(2).toString, // assert: ArrayToString
    Array("").toString, // assert: ArrayToString
    List(3).toString,
  )
}
