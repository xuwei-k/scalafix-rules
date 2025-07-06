/*
rule = TypeProjection
 */
package fix

trait TypeProjectionTest {
  type A

  def f1: TypeProjectionTest#A // assert: TypeProjection
}
