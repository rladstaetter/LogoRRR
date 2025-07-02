package app.logorrr.util

import net.ladstatt.util.log.CanLog
import org.scalatest.wordspec.AnyWordSpec

import java.util.logging.Level

class CanLogSpec extends AnyWordSpec with CanLog {

  "IsLogLevelSet correctly" in {
    assert(logLevel == Level.INFO)
  }

}
