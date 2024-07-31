package app.logorrr.model

import app.logorrr.util.CanLog
import app.logorrr.views.settings.timestamp.SimpleRange
import pureconfig.{ConfigReader, ConfigWriter}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}
import scala.util.{Failure, Success, Try}

object LogEntryInstantFormat extends CanLog {

  val DefaultPattern = "yyyy-MM-dd HH:mm:ss.nnnnnnnnn"
  /** just my preferred time format */
  val Default: LogEntryInstantFormat = LogEntryInstantFormat(SimpleRange(1, 24), DefaultPattern)

  implicit lazy val reader: ConfigReader[LogEntryInstantFormat] = deriveReader[LogEntryInstantFormat]
  implicit lazy val writer: ConfigWriter[LogEntryInstantFormat] = deriveWriter[LogEntryInstantFormat]

  def parseInstant(line: String, entrySetting: LogEntryInstantFormat): Option[Instant] =
    if (line.length >= entrySetting.endCol) {
      val dateTimeAsString = line.substring(entrySetting.startCol, entrySetting.endCol)
      Try {
        val dtf: DateTimeFormatter = entrySetting.dateTimeFormatter
        LocalDateTime.parse(dateTimeAsString, dtf).toInstant(ZoneOffset.of(entrySetting.zoneOffset))
      } match {
        case Success(value) => Option(value)
        case Failure(_) =>
          logTrace(s"Could not parse '$dateTimeAsString' at pos (${entrySetting.startCol}/${entrySetting.endCol}) with pattern '$${entrySetting.dateTimePattern}'")
          None
      }
    } else {
      None
    }


}

case class LogEntryInstantFormat(range: SimpleRange
                                 , dateTimePattern: String
                                 , zoneOffset: String = "+1") {
  val startCol: Int = range.start
  val endCol: Int = range.end
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.of(zoneOffset))


}