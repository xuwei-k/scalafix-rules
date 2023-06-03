package fix

/*
rule = AddExplicitImplicitTypes
 */
class AddExplicitImplicitTypesTest {
  class A

  implicit val a1 = new A

  implicit val a2 = new A {}
}
