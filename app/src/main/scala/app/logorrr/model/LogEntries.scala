package app.logorrr.model

import app.logorrr.model.LogEntries.logException
import app.logorrr.util.CanLog
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.paint.Color

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.time.Instant
import java.util
import java.util.stream
import java.util.stream.Collectors
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.jdk.CollectionConverters._

object LogFileReader {
  def readFromFile(logFile: Path): util.List[String] = {
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


}

/** Abstraction for a log file */
object LogEntries extends CanLog {

  case class TimeRange(startTime: Instant, endTime: Instant)

  def apply(parseEntryForTimeInstant: String => Option[Instant])(logFilePath: Path): LogEntries = timeR({
    var lineNumber: Int = 0
    // trying to be very clever and use java stream instead of scala collections
    // it makes a notable difference in performance if we don't convert huge lists from java <-> scala
    val logEntryStream: stream.Stream[LogEntry] = LogFileReader.readFromFile(logFilePath).stream().map(l => {
      lineNumber = lineNumber + 1
      LogEntry(lineNumber, Color.YELLOW, l, parseEntryForTimeInstant(l))
    })
    LogEntries(FXCollections.observableList(logEntryStream.collect(Collectors.toList[LogEntry]())))
  }, s"Imported ${logFilePath.toAbsolutePath.toString} ... ")

  def apply(logFilePath: Path, logEntryTimeFormat: LogEntryInstantFormat): LogEntries = {
    apply(l => LogEntryInstantFormat.parseInstant(l, logEntryTimeFormat))(logFilePath)
  }

  def apply(logFilePath: Path): LogEntries = apply(l => None)(logFilePath)


}


case class LogEntries(values: ObservableList[LogEntry]) {

  val timings: Map[Int, Instant] =
    values.stream().collect(Collectors.toMap((le: LogEntry) => le.lineNumber, (le: LogEntry) => le.someInstant.getOrElse(Instant.now()))).asScala.toMap

  /** contains time information for first and last entry for given log file */
  lazy val someTimeRange: Option[LogEntries.TimeRange] =
    if (values.isEmpty) {
      None
    } else {
      val filteredEntries = values.filtered((t: LogEntry) => t.someInstant.isDefined)
      if (filteredEntries.isEmpty) {
        None
      } else {
        for {start <- filteredEntries.get(0).someInstant
             end <- filteredEntries.get(filteredEntries.size() - 1).someInstant} yield LogEntries.TimeRange(start, end)
      }
    }



}


