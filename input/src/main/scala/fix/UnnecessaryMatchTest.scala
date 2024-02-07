/*
rule = UnnecessaryMatch
 */
package fix

class UnnecessaryMatchTest {
  List(1).map { a =>
    a match {
      case 2 => 3
      case 4 => 5 // comment 1
    }
  }

  List("a1").map(a =>
    a match {
      case "a2" => 7 // comment 2
      case "a3" => 8
    }
  )

  List(1).map { a =>
    a match {
      case 2 => a
    }
  }
}
