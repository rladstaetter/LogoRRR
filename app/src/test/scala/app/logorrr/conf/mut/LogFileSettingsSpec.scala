package app.logorrr.conf.mut

import app.logorrr.TestUtil
import app.logorrr.conf.CoreGen
import app.logorrr.io.FileId
import app.logorrr.model.{LogFileSettings, TimestampSettingsSpec}
import app.logorrr.views.Filter
import org.scalacheck.Gen


object LogFileSettingsSpec {

  val gen: Gen[LogFileSettings] = for {
    fileId <- Gen.identifier.map(FileId.apply)
    selectedIndex <- Gen.posNum[Int]
    firstOpened <- Gen.posNum[Long]
    dPos <- Gen.posNum[Double]
    filters <- Gen.listOf(TestUtil.filterGen.map(f => Filter(f.getPredicate.description, f.getColor, f.isActive)))
    leif <- TimestampSettingsSpec.gen
    someLogEntryInstantFormat <- Gen.oneOf(None, Option(leif))
    blockSettings <- BlockSettingsSpec.gen
    fontSize <- Gen.posNum[Int]
    autoScroll <- CoreGen.booleanGen
  } yield LogFileSettings(fileId
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
    , LogFileSettings.DefaultUpperTimestamp)
}
