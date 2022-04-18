package app.logorrr.conf.mut

import app.logorrr.conf.RecentFileSettings
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.views.{Filter, Fltr, SimpleRange}
import org.scalacheck.Gen

import scala.util.Random

object FilterSpec {

  val gen: Gen[Filter] = for {
    f <- Gen.oneOf(Filter.seq)
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
    dPos <- Gen.posNum[Double]
    filters <- Gen.listOf(FilterSpec.gen)
    leif <- LogEntryInstantFormatSpec.gen
    someLogEntryInstantFormat <- Gen.oneOf(None, Option(leif))
  } yield LogFileSettings(pathAsString, dPos, filters, someLogEntryInstantFormat)
}

object RecentFileSettingsSpec {

  val gen: Gen[RecentFileSettings] = for {
    lfd <- Gen.nonEmptyMap(LogFileSettingsSpec.gen.map(lfs => (lfs.pathAsString, lfs)))
    salr <- Gen.oneOf(None, {
      val keys = lfd.keys.toSeq
      val i = Random.nextInt(keys.length)
      val e: LogFileSettings = lfd.values.toSeq(i)
      Option(e.pathAsString)
    })
  } yield RecentFileSettings(lfd, salr)

}
