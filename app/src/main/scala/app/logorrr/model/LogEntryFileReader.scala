package app.logorrr.model

import app.logorrr.util.CanLog
import app.logorrr.views.search.Fltr
import javafx.collections.{FXCollections, ObservableList}

import java.nio.file.Path
import java.time.Instant
import java.util

/** Abstraction for a log file */
object LogEntryFileReader extends CanLog {

  private def mkLogEntryList(parseEntryForTimeInstant: String => Option[Instant])
                            (logFilePath: Path): ObservableList[LogEntry] = timeR({
    var lineNumber: Int = 0
    val arraylist = new util.ArrayList[LogEntry]()
    LogFileReader.readFromFile(logFilePath).map(l => {
      lineNumber = lineNumber + 1
      arraylist.add(LogEntry(lineNumber, l, parseEntryForTimeInstant(l)))
    })
    FXCollections.observableList(arraylist)
  }, s"Imported ${logFilePath.toAbsolutePath.toString} ... ")

  def from(logFilePath: Path, filters: Seq[Fltr], logEntryTimeFormat: LogEntryInstantFormat): ObservableList[LogEntry] = {
    mkLogEntryList(l => LogEntryInstantFormat.parseInstant(l, logEntryTimeFormat))(logFilePath)
  }

  def from(logFile: Path, filters: Seq[Fltr]): ObservableList[LogEntry] = mkLogEntryList(_ => None)(logFile)


}




