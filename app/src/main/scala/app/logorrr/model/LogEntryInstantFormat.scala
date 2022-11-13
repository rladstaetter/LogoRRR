package app.logorrr.model

import app.logorrr.views.settings.SimpleRange
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}
import scala.util.Try

object LogEntryInstantFormat {

  val DefaultPattern = "yyyy-MM-dd HH:mm:ss.SSS"
  /** just my preferred time format */
  val Default = LogEntryInstantFormat(SimpleRange(1, 24), DefaultPattern)

  implicit lazy val reader = deriveReader[LogEntryInstantFormat]
  implicit lazy val writer = deriveWriter[LogEntryInstantFormat]

  def parseInstant(line: String, entrySetting: LogEntryInstantFormat): Option[Instant] =
    if (line.length >= entrySetting.startColumn.end) {
      Try {
        val dateTimeAsString = line.substring(entrySetting.startColumn.start, entrySetting.startColumn.end)
        val dtf: DateTimeFormatter = entrySetting.dateTimeFormatter
        LocalDateTime.parse(dateTimeAsString, dtf).toInstant(ZoneOffset.of(entrySetting.zoneOffset))
      }.toOption
    } else None


}

case class LogEntryInstantFormat(startColumn: SimpleRange
                                 , dateTimePattern: String
                                 , zoneOffset: String = "+1") {

  val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.of(zoneOffset))
}