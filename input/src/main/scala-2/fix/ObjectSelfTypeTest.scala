package fix

/*
rule = ObjectSelfType
 */
object ObjectSelfTypeTest { self: AnyRef => } // assert: ObjectSelfType
