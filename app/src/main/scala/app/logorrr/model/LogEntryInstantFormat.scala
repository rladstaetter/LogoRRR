package app.logorrr.model

import app.logorrr.views.SimpleRange
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}
import java.time.format.DateTimeFormatter
import scala.util.Try

object LogEntryInstantFormat {

   val DefaultPattern = "yyyy-MM-dd HH:mm:ss.SSS"
  /** just my preferred time format */
  val Default = LogEntryInstantFormat(SimpleRange(1, 24), DefaultPattern)

  implicit lazy val reader = deriveReader[LogEntryInstantFormat]
  implicit lazy val writer = deriveWriter[LogEntryInstantFormat]

  def parseInstant(line: String, entrySetting: LogEntryInstantFormat): Option[Instant] = Try {
    val dateTimeAsString = line.substring(entrySetting.dateTimeRange.start, entrySetting.dateTimeRange.end)
    val dtf: DateTimeFormatter = entrySetting.dateTimeFormatter
    LocalDateTime.parse(dateTimeAsString, dtf).toInstant(ZoneOffset.of(entrySetting.zoneOffset))
  }.toOption


}

case class LogEntryInstantFormat(dateTimeRange: SimpleRange
                                 , dateTimePattern: String
                                 , zoneOffset: String = "+1") {

  val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.of(zoneOffset))
}