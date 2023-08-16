package app.logorrr

import app.logorrr.model.LogEntryFileReader
import app.logorrr.util.OsUtil
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Paths}


class LogEntryFileReaderSpec extends AnyWordSpec {

  "Logfile" when {
    "contains special chars" should {
      val p = Paths.get("src/test/resources/app/logorrr/logfile-with-encoding-error.log")
      //val p = Paths.get("src/test/resources/app/logorrr/util/orig.log")
      "exist" in assert(Files.exists(p))
      "be readable" in {
        if (!OsUtil.isMac) { // fixme: currently this guard exists since otherwise we would have to fiddle around loading native libs on mac
          val r = LogEntryFileReader.from(p, Seq())
          assert(!r.isEmpty)
        }
      }
    }
  }
}
