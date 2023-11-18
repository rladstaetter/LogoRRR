package app.logorrr.io

import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import javafx.collections.{FXCollections, ObservableList}

import java.nio.file.Path
import java.util

/**
 * Read a log file and parse time information from it
 */
object LogEntryFileReader {

  def from(logFile: Path, logEntryTimeFormat: LogEntryInstantFormat): ObservableList[LogEntry] = {
    var lineNumber: Int = 0
    val arraylist = new util.ArrayList[LogEntry]()
    FileManager.fromPathUsingSecurityBookmarks(logFile).map(l => {
      lineNumber = lineNumber + 1
      arraylist.add(LogEntry(lineNumber, l, LogEntryInstantFormat.parseInstant(l, logEntryTimeFormat)))
    })
    FXCollections.observableList(arraylist)
  }

}




