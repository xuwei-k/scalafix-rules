package fix


object ExplicitImplicitTypesTest2 {
  implicit def a1 = 2 
  implicit val a2 = 2 
  implicit lazy val a3 = 2 

  implicit def c1: Int = 2
  implicit val c2: Int = 2
  implicit lazy val c3: Int = 2

  def x1: Int = {
    implicit def d1 = 2 
    implicit val d2 = 2 
    implicit lazy val d3 = 2 
    1
  }

  val x2: Int = {
    implicit def d1 = 2 
    implicit val d2 = 2 
    implicit lazy val d3 = 2 
    2
  }

  var x3: Int = {
    implicit def d1 = 2 
    implicit val d2 = 2 
    implicit lazy val d3 = 2 
    3
  }
}
