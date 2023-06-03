package fix


class AddExplicitImplicitTypesTest {
  class A

  implicit val a1: A = new A

  implicit val a2: A = new A {}
}
