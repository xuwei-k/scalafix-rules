package fix

import scala.meta.Term
import scala.meta.Tree
import scalafix.Patch
import scalafix.v1.MethodSignature
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionTreeScalafix

class NamedParamOrder extends SemanticRule("NamedParamOrder") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    getPatch(doc.tree).asPatch
  }
  private[this] def getPatch(t: Tree)(implicit doc: SemanticDocument): List[Patch] = {
    t.collect {
      case t: Term.Apply if t.args.nonEmpty =>
        val named = t.args.collect {
          case Term.Assign(k: Term.Name, v) if getPatch(v).isEmpty =>
            k -> v
        }
        if (t.args.lengthCompare(named.size) == 0) {
          t.fun.symbol.info.flatMap { i =>
            PartialFunction
              .condOpt(i.signature) { case m: MethodSignature =>
                m.parameterLists.headOption.flatMap { define =>
                  val defNames = define.map(_.displayName)
                  val callNames = named.map(_._1.value)
                  if (
                    (defNames.lengthCompare(named.size) == 0) &&
                    (defNames.toSet == callNames.toSet) &&
                    (defNames != callNames)
                  ) {
                    val map = named.map { case (k, v) => k.value -> (k, v) }.toMap
                    val result = defNames.map(map)
                    Some(
                      t.args
                        .zip(result)
                        .map { case (oldParam, (k, v)) =>
                          Patch.replaceTree(oldParam, s"${k.syntax} = ${v}")
                        }
                        .asPatch
                    )
                  } else {
                    None
                  }
                }
              }
              .flatten
          }
        } else {
          None
        }
    }.flatten
  }
}
