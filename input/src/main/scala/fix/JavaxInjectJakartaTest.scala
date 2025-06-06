/*
rule = JavaxInjectJakarta
 */
package fix

import com.google.inject.Inject // assert: JavaxInjectJakarta
import com.google.inject.Singleton // assert: JavaxInjectJakarta
import com.google.inject.Scope // assert: JavaxInjectJakarta
import com.google.inject.Provider
import com.google.inject.name.Named // assert: JavaxInjectJakarta
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Guice
import jakarta.inject.Singleton
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Scope
import jakarta.inject.Provider
import jakarta.inject.Qualifier
import javax.inject.Singleton // assert: JavaxInjectJakarta
import javax.inject.Inject // assert: JavaxInjectJakarta
import javax.inject.Named // assert: JavaxInjectJakarta
import javax.inject.Scope // assert: JavaxInjectJakarta
import javax.inject.Provider
import javax.inject.Qualifier // assert: JavaxInjectJakarta

@javax.inject.Singleton // assert: JavaxInjectJakarta
class JavaxInjectJakartaTest1 @javax.inject.Inject() ( // assert: JavaxInjectJakarta
  @javax.inject.Named("a") x: Int // assert: JavaxInjectJakarta
)

@com.google.inject.Singleton // assert: JavaxInjectJakarta
class JavaxInjectJakartaTest2 @com.google.inject.Inject() ( // assert: JavaxInjectJakarta
  @com.google.inject.name.Named("b") x: Int // assert: JavaxInjectJakarta
)

@jakarta.inject.Singleton
class JavaxInjectJakartaTest3 @jakarta.inject.Inject() (
  @jakarta.inject.Named("c") x: Int
)

trait JavaxInjectJakartaTest4 extends jakarta.inject.Provider[Int]
trait JavaxInjectJakartaTest5 extends javax.inject.Provider[Int]
trait JavaxInjectJakartaTest6 extends com.google.inject.Provider[Int]
