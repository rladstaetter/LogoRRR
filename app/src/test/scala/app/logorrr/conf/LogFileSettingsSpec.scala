package app.logorrr.conf

import app.logorrr.TestUtil
import org.scalacheck.Gen


object LogFileSettingsSpec:

  val gen: Gen[LogFileSettings] = for
    fileId <- Gen.identifier.map(FileId.apply)
    selectedIndex <- Gen.posNum[Int]
    firstOpened <- Gen.posNum[Long]
    dPos <- Gen.posNum[Double]
    filters <- Gen.listOf(TestUtil.searchTermGen)
    leif <- TimestampSettingsSpec.gen
    someLogEntryInstantFormat <- Gen.oneOf(None, Option(leif))
    blockSettings <- BlockSettingsSpec.gen
    fontSize <- Gen.posNum[Int]
    autoScroll <- CoreGen.booleanGen
    someSelectedSearchTermGroup <- Gen.option(Gen.identifier)
  yield LogFileSettings(fileId
    , selectedIndex
    , firstOpened
    , dPos
    , fontSize
    , filters
    , blockSettings
    , someLogEntryInstantFormat
    , autoScroll
    , 0
    , 10
    , LogFileSettings.DefaultLowerTimestamp
    , LogFileSettings.DefaultUpperTimestamp
    , someSelectedSearchTermGroup
    , Map())
