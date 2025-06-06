/*
rule = JavaxInjectJakarta

JavaxInjectJakarta.allowProvider = false
 */
package fix

import com.google.inject.Inject // assert: JavaxInjectJakarta
import com.google.inject.Singleton // assert: JavaxInjectJakarta
import com.google.inject.Scope // assert: JavaxInjectJakarta
import com.google.inject.Provider // assert: JavaxInjectJakarta
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
import javax.inject.Provider // assert: JavaxInjectJakarta
import javax.inject.Qualifier // assert: JavaxInjectJakarta

@javax.inject.Singleton // assert: JavaxInjectJakarta
class JavaxInjectJakartaNotProviderTest1 @javax.inject.Inject() ( // assert: JavaxInjectJakarta
  @javax.inject.Named("a") x: Int // assert: JavaxInjectJakarta
)

@com.google.inject.Singleton // assert: JavaxInjectJakarta
class JavaxInjectJakartaNotProviderTest2 @com.google.inject.Inject() ( // assert: JavaxInjectJakarta
  @com.google.inject.name.Named("b") x: Int // assert: JavaxInjectJakarta
)

@jakarta.inject.Singleton
class JavaxInjectJakartaNotProviderTest3 @jakarta.inject.Inject() (
  @jakarta.inject.Named("c") x: Int
)

trait JavaxInjectJakartaNotProviderTest4 extends jakarta.inject.Provider[Int]
trait JavaxInjectJakartaNotProviderTest5 extends javax.inject.Provider[Int] // assert: JavaxInjectJakarta
trait JavaxInjectJakartaNotProviderTest6 extends com.google.inject.Provider[Int] // assert: JavaxInjectJakarta
