package app.logorrr.issues

import app.logorrr.io.IoManager
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Paths}



class Issue139Spec extends AnyWordSpec {

  "Logfile" when {
    "encodedInUtf16" should {
      val p = Paths.get("src/test/resources/app/logorrr/issues/issue-139.log")
      "exist" in assert(Files.exists(p))
      "can read file" in assert(IoManager.fromPath(p).nonEmpty)
    }
  }
}
