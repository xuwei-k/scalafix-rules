/*
rule = StringFormatToInterpolation
 */
package fix

class StringFormatToInterpolationTest {
  "a b c".format(9)

  " %s a1 %s a2 %s a3".format(2, Nil, false)

  "%s%s".format(2, "x")

  "%s%s%sa".format(9, 8, 7)

  "%s x %s".format(5, true)

  "%s".format(Some(9))

  "a %s".format(Some(7))

  "%s b".format(Some(4))

  "%s %s %s".format(1, None)
}
