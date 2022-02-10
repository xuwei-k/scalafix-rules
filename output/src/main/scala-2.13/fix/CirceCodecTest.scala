package fix


import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec



case class CirceCodecTest1(a: Int)

object CirceCodecTest1 {
implicit val codec: Codec.AsObject[CirceCodecTest1] = deriveCodec[CirceCodecTest1]

}


case class CirceCodecTest2(a: String)

object CirceCodecTest2 {
  implicit val codec: Codec.AsObject[CirceCodecTest2] = deriveCodec[CirceCodecTest2]
def foo = 2
}

