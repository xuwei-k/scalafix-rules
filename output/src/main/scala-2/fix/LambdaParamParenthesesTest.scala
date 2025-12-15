package fix

class LambdaParamParenthesesTest {
  def a1: Int => Int = { x => x }

  def a2: Int => Int = { x: Int => x } 

  def a3: Int => Int = { (x: Int) => x }
}
