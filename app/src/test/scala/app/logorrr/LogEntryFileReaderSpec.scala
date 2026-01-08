package app.logorrr

import app.logorrr.io.IoManager
import net.ladstatt.util.os.OsUtil
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Paths}


class LogEntryFileReaderSpec extends AnyWordSpec:

  "Logfile" when:
    "contains special chars" should:
      val p = Paths.get("src/test/resources/app/logorrr/logfile-with-encoding-error.log")
      "exist" in assert(Files.exists(p))
      "be readable" in:
        if !OsUtil.isMac then // fixme: currently this guard exists since otherwise we would have to fiddle around loading native libs on mac
          val r = IoManager.from(p)
          assert(!r.isEmpty)
