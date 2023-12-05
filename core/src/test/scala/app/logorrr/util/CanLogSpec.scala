package app.logorrr.util

import org.scalatest.wordspec.AnyWordSpec

import java.util.logging.Level

class CanLogSpec extends AnyWordSpec {

  "IsLogLevelSet correctly" in {
    assert(CanLog.LogLevel == Level.INFO)
  }

}
