package app.logorrr.model

import app.logorrr.util.CanLog
import app.logorrr.views.settings.timestamp.SimpleRange
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

import java.time._
import java.time.format.DateTimeFormatter
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
      val dtf: DateTimeFormatter = entrySetting.dateTimeFormatter
      Try {
        LocalDateTime.parse(dateTimeAsString, dtf).atZone(ZoneId.systemDefault).toInstant
      } match {
        case Success(value) => Option(value)
        case Failure(_) =>
          // retrying with localtime as fallback for entries which don't have any
          // date information (for example: '08:34:33' representing today morning)
          Try {
            LocalDateTime.of(LocalDate.now(), LocalTime.parse(dateTimeAsString, dtf)).atZone(ZoneId.systemDefault()).toInstant
          } match {
            case Success(value) => Option(value)
            case Failure(exception) =>
              logTrace(s"Could not parse '$dateTimeAsString' at pos (${entrySetting.startCol}/${entrySetting.endCol}) with pattern '${entrySetting.dateTimePattern}': ${exception.getMessage}")
              None
          }
      }
    } else {
      None
    }


}

case class LogEntryInstantFormat(range: SimpleRange, dateTimePattern: String) {
  val startCol: Int = range.start
  val endCol: Int = range.end
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.systemDefault)
}