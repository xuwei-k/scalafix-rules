/*
rule = JavaxInjectGuice
 */
package fix

import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.Scope
import com.google.inject.Provider
import com.google.inject.name.Named
import jakarta.inject.Singleton
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Scope
import jakarta.inject.Provider
import jakarta.inject.Qualifier
import javax.inject.Singleton // assert: JavaxInjectGuice
import javax.inject.Inject // assert: JavaxInjectGuice
import javax.inject.Named // assert: JavaxInjectGuice
import javax.inject.Scope // assert: JavaxInjectGuice
import javax.inject.Provider // assert: JavaxInjectGuice
import javax.inject.Qualifier // assert: JavaxInjectGuice

@javax.inject.Singleton // assert: JavaxInjectGuice
class JavaxInjectGuiceTest1 @javax.inject.Inject() ( // assert: JavaxInjectGuice
  @javax.inject.Named("a") x: Int // assert: JavaxInjectGuice
)

@com.google.inject.Singleton
class JavaxInjectGuiceTest2 @com.google.inject.Inject() (
  @com.google.inject.name.Named("b") x: Int
)

@jakarta.inject.Singleton
class JavaxInjectGuiceTest3 @jakarta.inject.Inject() (
  @jakarta.inject.Named("c") x: Int
)

trait JavaxInjectGuiceTest4 extends jakarta.inject.Provider[Int]
trait JavaxInjectGuiceTest5 extends javax.inject.Provider[Int] // assert: JavaxInjectGuice
trait JavaxInjectGuiceTest6 extends com.google.inject.Provider[Int]
