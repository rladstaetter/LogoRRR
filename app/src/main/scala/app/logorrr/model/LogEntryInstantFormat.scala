package app.logorrr.model

import app.logorrr.views.settings.timer.SimpleRange
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
    if (line.length >= entrySetting.range.end) {
      Try {
        val dateTimeAsString = line.substring(entrySetting.range.start, entrySetting.range.end)
        val dtf: DateTimeFormatter = entrySetting.dateTimeFormatter
        LocalDateTime.parse(dateTimeAsString, dtf).toInstant(ZoneOffset.of(entrySetting.zoneOffset))
      }.toOption
    } else None


}

case class LogEntryInstantFormat(range: SimpleRange
                                 , dateTimePattern: String
                                 , zoneOffset: String = "+1") {
  val startCol = range.start
  val endCol = range.end
  val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.of(zoneOffset))
}