package app.logorrr.views.text.contextactions

import app.logorrr.model.LogEntry
import javafx.collections.transformation.FilteredList
import javafx.scene.control.MenuItem
import net.ladstatt.util.log.CanLog

/**
 * Cuts away all entries following the current log entry.
 *
 * Selected log entry stays the same, also no change to current position (compare this to the IgnoreAbove action)
 *
 * @param currentEntry current selected log entry
 * @param filteredList filtered log entry list
 */
class IgnoreBelowMenuItem(currentEntry: LogEntry, filteredList: FilteredList[LogEntry]) extends MenuItem("Ignore entries below") with CanLog:

  setOnAction(_ => {
    val currPredicate = filteredList.getPredicate
    filteredList.setPredicate((entry: LogEntry) => currPredicate.test(entry) && entry.lineNumber <= currentEntry.lineNumber)
  })

