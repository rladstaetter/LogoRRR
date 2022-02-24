package app.logorrr.model

import app.logorrr.util.{CanLog, JfxUtils, LTailerListener}
import app.logorrr.views.{Filter, LogColumnDef}
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ObservableList}
import org.apache.commons.io.input.Tailer

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.util
import java.util.function.Predicate
import java.util.stream.Collectors
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}


/** Abstraction for a log file */
object LogReport extends CanLog {

  def apply(lrd: LogReportDefinition): LogReport = timeR({
    val logFile = lrd.path
    val filters = lrd.filters

    var lineNumber: Long = 0L
    // trying to be very clever and use java stream instead of scala collections
    // it makes a notable difference in performance if we don't convert huge lists from java <-> scala
    val logEntryStream = readFromFile(logFile).stream().map(l => {
      lineNumber = lineNumber + 1L
      lrd.someLogEntrySetting match {
        case Some(entrySetting) => LogEntry(lineNumber, l, tryToParseDateTime(l, entrySetting))
        case None =>
          LogEntry(lineNumber, l, None)
      }
    })
    new LogReport(logFile
      , FXCollections.observableList(logEntryStream.collect(Collectors.toList[LogEntry]()))
      , filters
      , lrd.someLogEntrySetting
      , lrd.active
      , lrd.dividerPosition)
  }, s"Imported ${lrd.path.toAbsolutePath.toString} ... ")

  private def tryToParseDateTime(line: String, entrySetting: LogEntrySetting): Option[Instant] = Try {
    val dateTimeAsString = line.substring(entrySetting.dateTimeRange.start, entrySetting.dateTimeRange.end)
    val dtf: DateTimeFormatter = entrySetting.dateTimeFormatter
    println(s"Parsing : '${dateTimeAsString}'")
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

case class TimeRange(startTime: Instant, endTime: Instant)

case class LogReport(path: Path
                     , entries: ObservableList[LogEntry]
                     , filters: Seq[Filter]
                     , someLogEntrySetting: Option[LogEntrySetting]
                     , active: Boolean
                     , dividerPosition: Double) {

  /** contains time information for first and last entry for given log file */
  lazy val someTimeRange: Option[TimeRange] =
    if (entries.isEmpty) {
      None
    } else {
      val filteredEntries = entries.filtered((t: LogEntry) => t.someInstant.isDefined)
      if (filteredEntries.isEmpty) {
        None
      } else {
        for {start <- filteredEntries.get(0).someInstant
             end <- filteredEntries.get(filteredEntries.size() - 1).someInstant} yield TimeRange(start, end)
      }
    }


  val logFileDefinition: LogReportDefinition =
    LogReportDefinition(path.toAbsolutePath.toString
      , None
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


