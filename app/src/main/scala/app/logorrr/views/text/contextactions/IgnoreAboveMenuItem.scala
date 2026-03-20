package app.logorrr.views.text.contextactions

import app.logorrr.model.{LogEntry, ScrollToActiveLogEntry}
import app.logorrr.views.text.LogTextView
import javafx.beans.property.{Property, SetPropertyBase}
import javafx.collections.transformation.FilteredList
import javafx.scene.control.MenuItem
import net.ladstatt.util.log.TinyLog

/**
 * Filters out all entries before the given current log entry and updates the current position.
 *
 * This filter will be reset if any other filter is changed in the filters menu bar.
 *
 * @param currentEntry               current entry
 * @param filteredList               filtered list
 */
class IgnoreAboveMenuItem(logTextView: LogTextView
                          , sharedSelection: SetPropertyBase[Int]
                          , currentEntry: LogEntry
                          , filteredList: FilteredList[LogEntry]) extends MenuItem("Ignore entries above") with TinyLog:
  setOnAction(_ => {
    val currPredicate = filteredList.getPredicate
    filteredList.setPredicate((entry: LogEntry) => currPredicate.test(entry) && currentEntry.lineNumber <= entry.lineNumber)
    sharedSelection.add(currentEntry.lineNumber)
    logTextView.fireEvent(ScrollToActiveLogEntry())
  })

