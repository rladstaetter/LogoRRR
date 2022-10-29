package app.logorrr.conf.mut

import app.logorrr.model.LogEntryInstantFormat
import org.scalacheck.Gen

object LogEntryInstantFormatSpec {
  val gen: Gen[LogEntryInstantFormat] = for {
    sr <- SimpleRangeSpec.gen
    dtp <- Gen.const(LogEntryInstantFormat.DefaultPattern)
    zo <- Gen.const("+1")
  } yield LogEntryInstantFormat(sr, dtp, zo)
}
