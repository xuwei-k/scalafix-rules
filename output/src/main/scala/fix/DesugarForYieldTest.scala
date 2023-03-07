package fix

class DesugarForYieldTest {
  def f1 = (List(1, 2)).flatMap{ a1 => (List("x", "y")).flatMap{ a2 => (List(true)).map{ a3 => (a1, a2, a3) } } }
}
