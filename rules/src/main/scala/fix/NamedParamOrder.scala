package fix

import fix.NamedParamOrder.ApplyOrNew
import scala.meta.Term
import scala.meta.Tree
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionSyntax
import scala.meta.tokens.Token
import scalafix.Patch
import scalafix.v1.ClassSignature
import scalafix.v1.MethodSignature
import scalafix.v1.SemanticDocument
import scalafix.v1.SemanticRule
import scalafix.v1.XtensionSeqPatch
import scalafix.v1.XtensionTreeScalafix

object NamedParamOrder {
  private object ApplyOrNew {

    def unapply(t: Term): Option[(String, Tree, List[Term])] = PartialFunction.condOpt(t) {
      case x: Term.Apply =>
        ("apply", x.fun, x.argClause.values)
      case x: Term.New =>
        ("<init>", x.init.tpe, x.init.argClauses.headOption.map(_.values).getOrElse(Nil))
    }
  }
}

class NamedParamOrder extends SemanticRule("NamedParamOrder") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    getPatch(doc.tree).asPatch
  }
  private[this] def getPatch(t: Tree)(implicit doc: SemanticDocument): List[Patch] = {
    t.collect {
      case t @ ApplyOrNew(methodName, fun, args) if args.nonEmpty && !t.tokens.exists(_.is[Token.Comment]) =>
        val named = args.collect {
          case Term.Assign(k: Term.Name, v) if getPatch(v).isEmpty =>
            k -> v
        }
        if (args.lengthCompare(named.size) == 0) {
          def f(m: MethodSignature): Option[Patch] = {
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
                  args
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

          fun.symbol.info.flatMap { i =>
            PartialFunction
              .condOpt(i.signature) {
                case m: MethodSignature =>
                  f(m)
                case c: ClassSignature =>
                  PartialFunction
                    .condOpt(
                      c.declarations.filter(_.displayName == methodName).map(_.signature).collect {
                        case m: MethodSignature => m
                      }
                    ) { case x :: Nil =>
                      f(x)
                    }
                    .flatten
              }
              .flatten
          }
        } else {
          None
        }
    }.flatten
  }
}
