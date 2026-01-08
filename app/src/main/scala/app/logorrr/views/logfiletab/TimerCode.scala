package app.logorrr.views.logfiletab

import app.logorrr.model.LogEntry
import app.logorrr.views.ops.time.TimeRange
import javafx.collections.ObservableList

/**
 * contains code to analyze timestamps
 */
trait TimerCode:
  def entries: ObservableList[LogEntry]

  /** contains time information for first and last entry for given log file */
  lazy val someTimeRange: Option[TimeRange] =
    if entries.isEmpty then
      None
    else
      val filteredEntries = entries.filtered((t: LogEntry) => t.someInstant.isDefined)
      if filteredEntries.isEmpty then
        None
      else
        for start <- filteredEntries.get(0).someInstant
             end <- filteredEntries.get(filteredEntries.size() - 1).someInstant yield TimeRange(start, end)
