package fix

/**
  * @tparam A0 a0
  * @tparam A1 a1
  * @tparam A2 a2
  */
abstract class TypeParamTest1[A1] {

  /**
    * @tparam B1 b1
    * @tparam B2 b2
    */
  def f1[B1](x: Int): Int

  /**
    * @tparam C1 c1
    * @tparam C4 c4
    */
  def f2[C4](x: Int): Int = x
}

/**
  * @tparam F1 f1
  * @tparam F3 f3
  */
trait A1[F1, F2]


/**
  * @tparam E3 e3
  * @tparam E1 e1
  */
enum A2[E1] {
  /**
    * @tparam E2 e2
    * @tparam E4 e4
    */
  case A3[E2] extends A2[E2]
}