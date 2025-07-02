package app.logorrr.model

import net.ladstatt.util.log.CanLog
import app.logorrr.views.settings.timestamp.SimpleRange
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

import java.time._
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}

object TimestampSettings extends CanLog {

  val DefaultPattern = "yyyy-MM-dd HH:mm:ss.SSS"

  val DefaultFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DefaultPattern)

  val Default: TimestampSettings = TimestampSettings(SimpleRange(1, 24), DefaultPattern)

  implicit lazy val reader: ConfigReader[TimestampSettings] = deriveReader[TimestampSettings]
  implicit lazy val writer: ConfigWriter[TimestampSettings] = deriveWriter[TimestampSettings]

  /**
   * Assumes that line contains a timestamp which encodes year/month/day hour/minute/second ... which hits
   * most of the usecases. However, for example if the log file uses relative timestamps this approach won't fit.
   *
   * @param line the string to parse
   * @param leif settings where to parse the timestamp, and in which format
   * @return
   */
  def parseInstant(line: String, leif: TimestampSettings): Option[Instant] =
    if (line.length >= leif.endCol) {
      val dateTimeAsString = line.substring(leif.startCol, leif.endCol)
      val dtf: DateTimeFormatter = leif.dateTimeFormatter
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
              logTrace(s"Could not be parsed: '$dateTimeAsString' at pos (${leif.startCol}/${leif.endCol}) using pattern '${leif.dateTimePattern}': ${exception.getMessage}")
              None
          }
      }
    } else {
      None
    }


}

case class TimestampSettings(range: SimpleRange, dateTimePattern: String) {
  val startCol: Int = range.start
  val endCol: Int = range.end
  val dateTimeFormatter: DateTimeFormatter = {
    // if we can't parse the provided pattern, fallback to default - even if we can't parse timestamps then
    Try(DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.systemDefault)).toOption.getOrElse(TimestampSettings.DefaultFormatter)
  }
}