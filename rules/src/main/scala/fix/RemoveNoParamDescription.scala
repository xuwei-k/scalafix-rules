package fix

import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.contrib._
import scala.meta.inputs.Input.VirtualFile

class RemoveNoParamDescription extends SyntacticRule("NoParamDescription") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t =>
        doc.comments
          .leading(t)
          .filter(
            _.docTokens.toList.flatten.exists(_.name.exists(_ contains "\n@param"))
          ).map { c =>
            val i = c.value.indexOf("@param")
            val start = c.start + i
            val end = start + c.value.drop(i).indexOf('\n') + 2
            val f = doc.input.asInstanceOf[VirtualFile]
            java.nio.file.Files.write(
              new java.io.File(f.path).toPath,
              (f.value.take(start) + f.value.drop(end)).getBytes("UTF-8")
            )
            Patch.empty
          }.asPatch
    }.asPatch
  }
}

