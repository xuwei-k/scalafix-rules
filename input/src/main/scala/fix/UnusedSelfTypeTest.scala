/*
rule = UnusedSelfType
 */
package fix

trait UnusedSelfTypeTest {
  class X1 { self => // assert: UnusedSelfType
  }
  trait X2 { self => // assert: UnusedSelfType
  }
  trait X3 { self =>
    def f1 = self
  }
  trait X4 { self =>
    trait B {
      val c = self
    }
  }
}
