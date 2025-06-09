/*
rule = Scala3Keyword
 */
package fix

object Scala3KeywordTest {
  class A1 {
    val enum = 1 // assert: Scala3Keyword
    def export = 2 // assert: Scala3Keyword
    var given = 3 // assert: Scala3Keyword
    lazy val then = 4 // assert: Scala3Keyword
  }

  class A2 {
    val `enum` = 1
    def `export` = 2
    var `given` = 3
    lazy val `then` = 4
  }

  class A3 {
    def f1[B](x: List[Option[B]]) = x.collect { case Some(enum) => enum } // assert: Scala3Keyword
    def f2[B](x: List[Option[B]]) = x.collect { case Some(export) => export } // assert: Scala3Keyword
    def f3[B](x: List[Option[B]]) = x.collect { case Some(given) => given } // assert: Scala3Keyword
    def f4[B](x: List[Option[B]]) = x.collect { case Some(then) => then } // assert: Scala3Keyword
  }
}
