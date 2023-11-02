package app.logorrr.model

import app.logorrr.util.CanLog
import javafx.collections.{FXCollections, ObservableList}

import java.nio.file.Path
import java.time.Instant
import java.util

/** Abstraction for a log file */
object LogEntryFileReader extends CanLog {

  private def mkLogEntryList(parseEntryForTimeInstant: String => Option[Instant])
                            (logFilePath: Path): ObservableList[LogEntry] = {
    var lineNumber: Int = 0
    val arraylist = new util.ArrayList[LogEntry]()
    LogFileReader.readFromFile(logFilePath).map(l => {
      lineNumber = lineNumber + 1
      arraylist.add(LogEntry(lineNumber, l, parseEntryForTimeInstant(l)))
    })
    FXCollections.observableList(arraylist)
  }

  def from(logFilePath: Path,  logEntryTimeFormat: LogEntryInstantFormat): ObservableList[LogEntry] = {
    mkLogEntryList(l => LogEntryInstantFormat.parseInstant(l, logEntryTimeFormat))(logFilePath)
  }

  def from(logFile: Path): ObservableList[LogEntry] = mkLogEntryList(_ => None)(logFile)


}




