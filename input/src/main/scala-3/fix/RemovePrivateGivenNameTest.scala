/*
rule = RemovePrivateGivenName
 */
package fix

object RemovePrivateGivenNameTest {

  given a1: Int = 1

  private given a2: Int = 2

  private[fix] given a3: Int = 3

  private given a4: Int = 4

  private given aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa5
    : String = "b"

  def f: Int = a4
}
