/*
rule = ArrayToString
 */
package fix

object ArrayToStringTest2 {
  def f1: Seq[String] = Seq(
    (Array(1) ++ Array(2)).toString, // assert: ArrayToString
    Array.fill(2)(3).toString, // assert: ArrayToString
    Array.range(1, 10).toString, // assert: ArrayToString
  )
}
