package app.logorrr

import app.logorrr.model.LogEntryFileReader
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Paths}


class LogEntryFileReaderSpec extends AnyWordSpec {

  "Logfile" when {
    "contains special chars" should {
      val p = Paths.get("src/test/resources/app/logorrr/logfile-with-encoding-error.log")
      //val p = Paths.get("src/test/resources/app/logorrr/util/orig.log")
      "exist" in assert(Files.exists(p))
      "be readable" in {
        val r = LogEntryFileReader.from(p, Seq())
        assert(!r.isEmpty)
      }
    }
  }
}
