/*
rule = ObjectFinal
 */
package fix

object ObjectFinalTest {
  final val a1: List[String] = List.empty[String] // assert: ObjectFinal
  final val a2 = List.empty[String]

  final def b1: List[String] = List.empty[String] // assert: ObjectFinal
  final def b2 = List.empty[String] // assert: ObjectFinal
  final def b3 = "a" // assert: ObjectFinal
  final def b4 = 123 // assert: ObjectFinal

  final val c1: Boolean = false
  final val c2: Byte = 2
  final val c3: Short = 3
  final val c4: Char = 'x'
  final val c5: Int = 5
  final val c6: Long = 6L
  final val c7: Float = 1.5f
  final val c8: Double = 2.5

  final val d1: scala.Boolean = false
  final val d2: scala.Byte = 2
  final val d3: scala.Short = 3
  final val d4: scala.Char = 'x'
  final val d5: scala.Int = 5
  final val d6: scala.Long = 6L
  final val d7: scala.Float = 1.5f
  final val d8: scala.Double = 2.5

  final val e1: _root_.scala.Boolean = false
  final val e2: _root_.scala.Byte = 2
  final val e3: _root_.scala.Short = 3
  final val e4: _root_.scala.Char = 'x'
  final val e5: _root_.scala.Int = 5
  final val e6: _root_.scala.Long = 6L
  final val e7: _root_.scala.Float = 1.5f
  final val e8: _root_.scala.Double = 2.5

  final val s1: String = "s"
  final val s2: Predef.String = "s"
  final val s3: scala.Predef.String = "s"
  final val s4: _root_.scala.Predef.String = "s"
  final val s5: java.lang.String = "s"
  final val s6: _root_.java.lang.String = "s"
}
