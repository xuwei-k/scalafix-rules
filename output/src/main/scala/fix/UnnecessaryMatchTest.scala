package fix

class UnnecessaryMatchTest {
  List(1).map { {
  case 2 => 3
  case 4 => 5
}
  }

  List("a1").map({
  case "a2" => 7
  case "a3" => 8
}
  )

  List(1).map { a =>
    a match {
      case 2 => a
    }
  }
}
