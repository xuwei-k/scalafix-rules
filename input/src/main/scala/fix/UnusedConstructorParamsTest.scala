package fix

/*
rule = UnusedConstructorParams
 */
class A1(val x: Int)

class A2(x: Int) // assert: UnusedConstructorParams

class A3(`type`: Int) extends A1(`type`)

class A4(var x: Int)

class A5(val x: Int)(implicit x2: String)
