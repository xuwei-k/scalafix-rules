package fix

object EnumDerivesCanEqualTest {
  enum E1 derives CanEqual {
    case X1
  }

  enum E2 extends C1 derives CanEqual {
    case X1
  }

  enum E3 extends C1 with C2 derives CanEqual {
    case X1
  }

  enum E4 derives B, CanEqual {
    case X1
  }

  enum E5 derives A, B, CanEqual {
    case X1
  }

  enum E6 extends C1, C2 derives A, B, CanEqual {
    case X1
  }

  enum E7(val x: Int) derives CanEqual {
    case X1 extends E7(9)
  }

  enum E8[M] derives CanEqual {
    case X1 extends E8[Long]
  }

  class A[X]

  object A {
    def derived[X]: A[X] = new A[X]
  }

  class B[X]

  object B {
    def derived[X]: B[X] = new B[X]
  }

  trait C1
  trait C2
}
