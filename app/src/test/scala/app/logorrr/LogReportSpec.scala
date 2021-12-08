package app.logorrr

import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Paths}


class LogReportSpec extends AnyWordSpec {

  "Logfile" when {
    "contains special chars" should {
      val p = Paths.get("src/test/resources/app/logorrr/logfile-with-encoding-error.log")
      //val p = Paths.get("src/test/resources/app/logorrr/util/orig.log")
      "exist" in assert(Files.exists(p))
      "be readable" in {
        val r = LogReport(p)
        assert(!r.entries.isEmpty)
      }
    }
  }
}
