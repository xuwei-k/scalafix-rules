/*
rule = CirceCodec
 */
package fix

import io.circe.generic.JsonCodec

@JsonCodec
case class CirceCodecTest1(a: Int)

@JsonCodec
case class CirceCodecTest2(a: String)

object CirceCodecTest2 {
  def foo = 2
}

@JsonCodec(encodeOnly = true)
case class CirceCodecTest3(a: Boolean)

@JsonCodec(decodeOnly = true)
case class CirceCodecTest4(a: Boolean)

object CirceCodecTest4 {
  val bar = Option(9)
}
