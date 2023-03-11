package fix

import scala.meta.Term
import scalafix.Patch
import scalafix.v1.MethodSignature
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionTreeScalafix

class NamedParamOrder extends SemanticRule("NamedParamOrder") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect { case t: Term.Apply =>
      val named = t.args.collect { case Term.Assign(k: Term.Name, v) =>
        k.value -> v
      }
      if (t.args.size == named.size) {
        t.fun.symbol.info.flatMap { i =>
          PartialFunction.condOpt(i.signature) { case m: MethodSignature =>
            m.parameterLists.headOption.map { define =>
              val defNames = define.map(_.displayName)
              val callNames = named.map(_._1)
              if (
                (defNames.size == named.size) &&
                (defNames.toSet == callNames.toSet) &&
                (defNames != callNames)
              ) {
                val map = named.toMap
                val result = defNames.map(k => k -> map(k))
                t.args
                  .zip(result)
                  .map { case (oldParam, (k, v)) =>
                    Patch.replaceTree(oldParam, s"${k} = ${v}")
                  }
                  .asPatch
              } else {
                Patch.empty
              }
            }.asPatch
          }
        }.asPatch
      } else {
        Patch.empty
      }
    }.asPatch
  }
}
