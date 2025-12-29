/*
rule = FileNameConsistent
 */
package fix

enum MyTopLevelEnum { // assert: FileNameConsistent
  case X1(x: Int)
  case X2(x: String)
}
