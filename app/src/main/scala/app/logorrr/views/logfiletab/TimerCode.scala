package app.logorrr.views.logfiletab

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList

trait TimerCode {
  def logEntries: ObservableList[LogEntry]

  /** contains time information for first and last entry for given log file */
  lazy val someTimeRange: Option[TimeRange] =
    if (logEntries.isEmpty) {
      None
    } else {
      val filteredEntries = logEntries.filtered((t: LogEntry) => t.someInstant.isDefined)
      if (filteredEntries.isEmpty) {
        None
      } else {
        for {start <- filteredEntries.get(0).someInstant
             end <- filteredEntries.get(filteredEntries.size() - 1).someInstant} yield TimeRange(start, end)
      }
    }
}
