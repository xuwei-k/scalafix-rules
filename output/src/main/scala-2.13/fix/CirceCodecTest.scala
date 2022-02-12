package fix


import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveCodec
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder



case class CirceCodecTest1(a: Int)

object CirceCodecTest1 {

  implicit val codec: Codec.AsObject[CirceCodecTest1] = deriveCodec[CirceCodecTest1]

}


case class CirceCodecTest2(a: String)

object CirceCodecTest2 {
  def foo = 2

  implicit val codec: Codec.AsObject[CirceCodecTest2] = deriveCodec[CirceCodecTest2]

}


case class CirceCodecTest3(a: Boolean)

object CirceCodecTest3 {

  implicit val encoder: Encoder.AsObject[CirceCodecTest3] = deriveEncoder[CirceCodecTest3]

}


case class CirceCodecTest4(a: Boolean)

object CirceCodecTest4 {
  val bar = Option(9)

  implicit val decoder: Decoder[CirceCodecTest4] = deriveDecoder[CirceCodecTest4]

}


case class CirceCodecTest5[A, B](a: A, b: B, i: Int)

object CirceCodecTest5 {

  implicit def codec[A, B](implicit A: Codec[A], B: Codec[B]): Codec.AsObject[CirceCodecTest5[A, B]] = deriveCodec[CirceCodecTest5[A, B]]

}


case class CirceCodecTest6[A <: List[Int]](a1: A, a2: A)

object CirceCodecTest6 {

  implicit def encoder[A <: List[Int]](implicit A: Encoder[A]): Encoder.AsObject[CirceCodecTest6[A]] = deriveEncoder[CirceCodecTest6[A]]

}


case class CirceCodecTest7[A](a: A)

object CirceCodecTest7 {

  implicit def decoder[A](implicit A: Decoder[A]): Decoder[CirceCodecTest7[A]] = deriveDecoder[CirceCodecTest7[A]]

}
