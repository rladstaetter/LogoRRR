package app.logorrr.conf.mut

import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.views.{Filter, SimpleRange}
import org.scalacheck.Gen

import scala.util.Random

object FilterSpec {

  val gen: Gen[Filter] = for {
    f <- Gen.oneOf(LogFileSettings.DefaultFilter)
  } yield f
}

object SimpleRangeSpec {

  val gen: Gen[SimpleRange] = for {
    start <- Gen.posNum[Int]
    end <- Gen.chooseNum(start, Random.nextInt(start + 100) + start)
  } yield SimpleRange(start, end)
}

object LogEntryInstantFormatSpec {
  val gen: Gen[LogEntryInstantFormat] = for {
    sr <- SimpleRangeSpec.gen
    dtp <- Gen.const(LogEntryInstantFormat.DefaultPattern)
    zo <- Gen.const("+1")
  } yield LogEntryInstantFormat(sr, dtp, zo)
}

object LogFileSettingsSpec {

  val gen: Gen[LogFileSettings] = for {
    pathAsString <- Gen.identifier
    selectedIndex <- Gen.posNum[Int]
    firstOpened <- Gen.posNum[Long]
    dPos <- Gen.posNum[Double]
    filters <- Gen.listOf(FilterSpec.gen)
    leif <- LogEntryInstantFormatSpec.gen
    someLogEntryInstantFormat <- Gen.oneOf(None, Option(leif))
    blockSettings <- BlockSettingsSpec.gen
  } yield LogFileSettings(pathAsString, selectedIndex, firstOpened, dPos, filters, blockSettings, someLogEntryInstantFormat)
}


