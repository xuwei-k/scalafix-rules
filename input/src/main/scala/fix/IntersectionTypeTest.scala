/*
rule = IntersectionType
 */
package fix

trait IntersectionTypeTest[A, B] {
  def f1: A with B // assert: IntersectionType
  def f2: A & B
}
