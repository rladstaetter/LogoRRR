package app.logorrr

import app.logorrr.io.FileManager
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Paths}



class Issue139Spec extends AnyWordSpec {

  "Logfile" when {
    "encodedInUtf16" should {
      val p = Paths.get("src/test/resources/app/logorrr/issue-139.log")
      //val p = Paths.get("src/test/resources/app/logorrr/util/orig.log")
      "exist" in assert(Files.exists(p))
      "can read file" in assert(FileManager.fromPath(p).nonEmpty)
    }
  }
}
