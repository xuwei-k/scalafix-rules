/*
rule = ObjectSelfType
 */
package fix

object ObjectSelfTypeTest { self: AnyRef => } // assert: ObjectSelfType
