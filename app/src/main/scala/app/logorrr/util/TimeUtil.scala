package app.logorrr.util

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{LogoRRRGlobals, TimeSettings}
import app.logorrr.model.LogEntry
import app.logorrr.views.ops.time.TimeRange
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.collections.ObservableList
import net.ladstatt.util.log.TinyLog

import java.time.*
import java.time.format.DateTimeFormatter
import java.util
import java.util.Optional
import scala.util.{Failure, Success, Try}

object TimeUtil extends TinyLog:

  /**
   * Assumes that line contains a timestamp which encodes year/month/day hour/minute/second ... which hits
   * most of the usecases. However, for example if the log file uses relative timestamps this approach won't fit.
   *
   * @param line the string to parse
   * @param leif settings where to parse the timestamp, and in which format
   * @return
   */
  def parseInstant(line: String, leif: TimeSettings): Option[Instant] =
    if line.length >= leif.endCol && leif.isValid then
      val dateTimeAsString = line.substring(leif.startCol, leif.endCol)
      val dtf: DateTimeFormatter = TimeUtil.mkDateTimeFormatter(leif.dateTimePattern)
      Try:
        LocalDateTime.parse(dateTimeAsString, dtf).atZone(ZoneId.systemDefault).toInstant
      match
        case Success(value) => Option(value)
        case Failure(_) =>
          // retrying with localtime as fallback for entries which don't have any
          // date information (for example: '08:34:33' representing today morning)
          Try:
            LocalDateTime.of(LocalDate.now(), LocalTime.parse(dateTimeAsString, dtf)).atZone(ZoneId.systemDefault()).toInstant
          match
            case Success(value) => Option(value)
            case Failure(exception) =>
              logWarn(s"Could not be parsed: '$dateTimeAsString' at pos (${leif.startCol}/${leif.endCol}) using pattern '${leif.dateTimePattern}': ${exception.getMessage}")
              None
    else None

  def mkDateTimeFormatter(dateTimePattern: String): DateTimeFormatter =
    Try(DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.systemDefault)).toOption.getOrElse(TimeSettings.DefaultFormatter)


  def calculate(mutLogFileSettings: MutLogFileSettings
                , chunkListView: ChunkListView[LogEntry]
                , logEntries: ObservableList[LogEntry]
                , timeSettings: TimeSettings
                , tsRegion: TimestampSettingsRegion): Unit = {
    mutLogFileSettings.setTimeSettings(timeSettings)
    var someFirstEntryTimestamp: Option[Instant] = None

    val tempList = new util.ArrayList[LogEntry]()
    for i <- 0 until logEntries.size() do {
      val e = logEntries.get(i)
      val someInstant = TimeUtil.parseInstant(e.value, timeSettings)
      if someFirstEntryTimestamp.isEmpty then {
        someFirstEntryTimestamp = someInstant
      }
      tempList.add(e.copy(someEpochMilli = someInstant.map(_.toEpochMilli)))
    }
    logEntries.setAll(tempList)
    val (min, max) = calcRange(logEntries)
    tsRegion.initializeRanges(min, max)
    tsRegion.setSliderPositions(min, max)

  }

  def calcRange(logEntries: java.util.List[LogEntry]): (Long, Long) =
    TimeUtil.calcTimeInfo(logEntries).getOrElse(TimeRange.defaultTimeRange)


  def calcTimeInfo(logEntries: util.List[LogEntry]): Option[(Long, Long)] =
    if logEntries.size > 1 then
      val min = calcMin(logEntries)
      val max = calcMax(logEntries)
      if (min == 0 || max == 0) None else Option((min, max))
    else None

  private def calcMax(logEntries: util.List[LogEntry]): Long = calcMin(logEntries.reversed)

  private def calcMin(logEntries: util.List[LogEntry]): Long =
    val first: Optional[LogEntry] = logEntries.stream.dropWhile(e => e.someEpochMilli.isEmpty).findFirst()
    if first.isPresent then first.get().someEpochMilli.get else 0L
