/*
rule = UnusedConstructorParams
 */
package fix

class A6(val x: Int)(using x2: String)

trait TraitParamTest1(x: Int) // assert: UnusedConstructorParams
