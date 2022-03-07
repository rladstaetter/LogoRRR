package app.logorrr.model

import app.logorrr.util.{CanLog, JfxUtils, LTailerListener}
import app.logorrr.views.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ObservableList}
import org.apache.commons.io.input.Tailer

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util
import java.util.stream.Collectors
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.{Duration, DurationLong}
import scala.jdk.CollectionConverters.{CollectionHasAsScala, IterableHasAsJava}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.jdk.CollectionConverters._

/** Abstraction for a log file */
object LogFile extends CanLog {

  case class TimeRange(startTime: Instant, endTime: Instant)

  def apply(logFileDefinition: LogFileDefinition): LogFile = timeR({
    val logFilePath = logFileDefinition.path
    val filters = logFileDefinition.filters

    var lineNumber: Long = 0L
    // trying to be very clever and use java stream instead of scala collections
    // it makes a notable difference in performance if we don't convert huge lists from java <-> scala
    val logEntryStream = readFromFile(logFilePath).stream().map(l => {
      lineNumber = lineNumber + 1L
      logFileDefinition.someLogEntrySetting match {
        case Some(entrySetting) => {
          val maybeInstant = tryToParseDateTime(l, entrySetting)
          maybeInstant.foreach(i => {})
          //   timings = timings + (lineNumber -> maybeInstant)
          LogEntry(lineNumber, l, maybeInstant)
        }
        case None => LogEntry(lineNumber, l, None)
      }
    })

    val entries: util.List[LogEntry] = logEntryStream.collect(Collectors.toList[LogEntry]())
    new LogFile(logFilePath
      , FXCollections.observableList(entries)
      , filters
      , logFileDefinition.someLogEntrySetting
      , logFileDefinition.active
      , logFileDefinition.dividerPosition)
  }, s"Imported ${logFileDefinition.path.toAbsolutePath.toString} ... ")

  private def tryToParseDateTime(line: String, entrySetting: LogEntrySetting): Option[Instant] = Try {
    val dateTimeAsString = line.substring(entrySetting.dateTimeRange.start, entrySetting.dateTimeRange.end)
    val dtf: DateTimeFormatter = entrySetting.dateTimeFormatter
    LocalDateTime.parse(dateTimeAsString, dtf).toInstant(ZoneOffset.of(entrySetting.zoneOffset))
  }.toOption

  private def readFromFile(logFile: Path): util.List[String] = {
    Try {
      Files.readAllLines(logFile)
    } match {
      case Failure(exception) =>
        val msg = s"Could not read file ${logFile.toAbsolutePath.toString} with default charset, retrying with ISO_8859_1 ..."
        logException(msg, exception)
        Try {
          Files.readAllLines(logFile, StandardCharsets.ISO_8859_1)
        } match {
          case Failure(exception) =>
            val msg = s"Could not read file ${logFile.toAbsolutePath.toString} properly. Reason: ${exception.getMessage}."
            logException(msg, exception)
            util.Arrays.asList(msg)
          case Success(value) =>
            value
        }
      case Success(lines) => lines
    }
  }

  def indexOf(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): Int = y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth

}


case class LogFile(path: Path
                   , entries: ObservableList[LogEntry]
                   , filters: Seq[Filter]
                   , someLogEntrySetting: Option[LogEntrySetting]
                   , active: Boolean
                   , dividerPosition: Double) {

  val timings: Map[Long, Instant] =
    entries.stream().collect(Collectors.toMap((le: LogEntry) => le.lineNumber, (le: LogEntry) => le.someInstant.getOrElse(Instant.now()))).asScala.toMap


  /** contains time information for first and last entry for given log file */
  lazy val someTimeRange: Option[LogFile.TimeRange] =
    if (entries.isEmpty) {
      None
    } else {
      val filteredEntries = entries.filtered((t: LogEntry) => t.someInstant.isDefined)
      if (filteredEntries.isEmpty) {
        None
      } else {
        for {start <- filteredEntries.get(0).someInstant
             end <- filteredEntries.get(filteredEntries.size() - 1).someInstant} yield LogFile.TimeRange(start, end)
      }
    }


  val logFileDefinition: LogFileDefinition =
    LogFileDefinition(path.toAbsolutePath.toString
      , active
      , dividerPosition
      , filters
      , someLogEntrySetting)

  val lengthProperty = new SimpleIntegerProperty(entries.size())

  val titleProperty = new SimpleStringProperty(computeTabName)

  val tailer = new Tailer(path.toFile, new LTailerListener(entries), 1000, true)

  /** start observing log file for changes */
  def init(): Unit = new Thread(tailer).start()

  /** stop observing changes */
  def stop(): Unit = tailer.stop()

  private def computeTabName = path.getFileName.toString + " (" + entries.size + " entries)"

  // if anything changes in entries list:
  //
  // - recompute title name
  // - recompute total size
  //
  entries.addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      JfxUtils.execOnUiThread {
        titleProperty.set(computeTabName)
        lengthProperty.set(entries.size)
      }
    }
  })


}


