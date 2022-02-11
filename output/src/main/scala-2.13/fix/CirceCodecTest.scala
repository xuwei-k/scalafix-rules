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
  implicit val codec: Codec.AsObject[CirceCodecTest2] = deriveCodec[CirceCodecTest2]
def foo = 2
}


case class CirceCodecTest3(a: Boolean)

object CirceCodecTest3 {
implicit val encoder: Encoder.AsObject[CirceCodecTest3] = deriveEncoder[CirceCodecTest3]

}


case class CirceCodecTest4(a: Boolean)

object CirceCodecTest4 {
  implicit val decoder: Decoder[CirceCodecTest4] = deriveDecoder[CirceCodecTest4]
val bar = Option(9)
}



