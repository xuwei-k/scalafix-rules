package fix

/*
rule = RemoveEmptyObject
 */
object RemoveEmptyObjectTest1

class RemoveEmptyObjectTest2

object RemoveEmptyObjectTest3 extends RemoveEmptyObjectTest2

object RemoveEmptyObjectTest4 {}

object RemoveEmptyObjectTest5 {
  def foo = "a"
}
