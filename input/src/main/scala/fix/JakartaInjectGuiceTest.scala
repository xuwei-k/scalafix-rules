/*
rule = JakartaInjectGuice
 */
package fix

import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.Scope
import com.google.inject.Provider
import com.google.inject.name.Named
import jakarta.inject.Singleton // assert: JakartaInjectGuice
import jakarta.inject.Inject // assert: JakartaInjectGuice
import jakarta.inject.Named // assert: JakartaInjectGuice
import jakarta.inject.Scope // assert: JakartaInjectGuice
import jakarta.inject.Provider // assert: JakartaInjectGuice
import jakarta.inject.Qualifier // assert: JakartaInjectGuice
import javax.inject.Singleton
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Scope
import javax.inject.Provider
import javax.inject.Qualifier

@javax.inject.Singleton
class JakartaInjectGuiceTest1 @javax.inject.Inject() (
  @javax.inject.Named("a") x: Int
)

@com.google.inject.Singleton
class JakartaInjectGuiceTest2 @com.google.inject.Inject() (
  @com.google.inject.name.Named("b") x: Int
)

@jakarta.inject.Singleton // assert: JakartaInjectGuice
class JakartaInjectGuiceTest3 @jakarta.inject.Inject() ( // assert: JakartaInjectGuice
  @jakarta.inject.Named("c") x: Int // assert: JakartaInjectGuice
)

trait JakartaInjectGuiceTest4 extends jakarta.inject.Provider[Int] // assert: JakartaInjectGuice
trait JakartaInjectGuiceTest5 extends javax.inject.Provider[Int]
trait JakartaInjectGuiceTest6 extends com.google.inject.Provider[Int]
