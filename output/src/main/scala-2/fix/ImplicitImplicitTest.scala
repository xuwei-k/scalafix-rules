package fix

object ImplicitImplicitTest {
  class A(implicit
    a: Int,
    val b: String,
    c: Long,
    private val d: Boolean
  )
}
