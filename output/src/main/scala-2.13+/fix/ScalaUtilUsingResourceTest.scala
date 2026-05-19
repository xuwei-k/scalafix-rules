package fix

import java.io.FileInputStream
import scala.util.Using

object ScalaUtilUsingResourceTest {
  def f1: Int = {
    
    Using.resource(new FileInputStream("a")) { a =>
       val b = a.read()
      val c = a.read()
      b + c
    }  
  }

  def f2: Int = {
    
    Using.resource(new FileInputStream("a")) { a =>
       val b = a.read()
      val c = a.read()
      b + c
    } 
      
  }

  def f3: String = {
    
    Using.resource(new FileInputStream("a")) { a =>
      a.toString
    }
      
  }

  def f4: Int = {
    val a1 = new FileInputStream("1")
    try {
      
      Using.resource(new FileInputStream("2")) { a2 =>
         a1.read + a2.read
      } 
        
    } finally
      a1.close()
  }
}
