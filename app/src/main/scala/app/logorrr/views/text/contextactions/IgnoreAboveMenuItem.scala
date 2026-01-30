package app.logorrr.views.text.contextactions

import app.logorrr.model.LogEntry
import javafx.beans.property.Property
import javafx.collections.transformation.FilteredList
import javafx.scene.control.MenuItem
import net.ladstatt.util.log.TinyLog

/**
 * Filters out all entries before the given current log entry and updates the current position.
 *
 * This filter will be reset if any other filter is changed in the filters menu bar.
 *
 * @param selectedLineNumberProperty contians number of currently selected line
 * @param currentEntry               current entry
 * @param filteredList               filtered list
 * @param scrollToActiveLogEntry     function to scroll to current active log entry
 */
class IgnoreAboveMenuItem(selectedLineNumberProperty: Property[Number]
                          , currentEntry: LogEntry
                          , filteredList: FilteredList[LogEntry]
                          , scrollToActiveLogEntry: () => Unit) extends MenuItem("Ignore entries above") with TinyLog:
  setOnAction(_ => {
    val currPredicate = filteredList.getPredicate
    filteredList.setPredicate((entry: LogEntry) => currPredicate.test(entry) && currentEntry.lineNumber <= entry.lineNumber)
    selectedLineNumberProperty.setValue(currentEntry.lineNumber)
    scrollToActiveLogEntry()
  })

