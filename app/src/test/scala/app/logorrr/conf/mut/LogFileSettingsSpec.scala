package app.logorrr.conf.mut

import app.logorrr.conf.CoreGen
import app.logorrr.model.LogFileSettings
import org.scalacheck.Gen


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
    fontSize <- Gen.posNum[Int]
    autoScroll <- CoreGen.booleanGen
  } yield LogFileSettings(pathAsString, selectedIndex, firstOpened, dPos, fontSize, filters, blockSettings, someLogEntryInstantFormat, autoScroll)
}
