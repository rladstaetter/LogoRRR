package app.logorrr.conf

import org.scalacheck.Gen

object LogFileSettingsSpec:

  val searchTermGen: Gen[SearchTerm] = for f <- Gen.oneOf(DefaultSearchTermGroups().searchTermGroups.head.terms) yield f

  val gen: Gen[LogFileSettings] = for
    fileId <- Gen.identifier.map(FileId.apply)
    selectedIndex <- Gen.posNum[Int]
    firstOpened <- Gen.posNum[Long]
    dPos <- Gen.posNum[Double]
    filters <- Gen.listOf(searchTermGen)
    leif <- TimeSettingsSpec.gen
    someLogEntryInstantFormat <- Gen.oneOf(None, Option(leif))
    blockSize <- Gen.posNum[Int]
    fontSize <- Gen.posNum[Int]
    autoScroll <- CoreGen.booleanGen
  yield LogFileSettings(fileId
    , Set()
    , firstOpened
    , dPos
    , fontSize
    , filters
    , blockSize
    , someLogEntryInstantFormat
    , autoScroll
    , 0
    , 10
    , LogFileSettings.DefaultLowerTimestamp
    , LogFileSettings.DefaultUpperTimestamp
    , true
    , 0L)
