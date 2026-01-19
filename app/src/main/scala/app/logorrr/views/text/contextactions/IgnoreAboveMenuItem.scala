package app.logorrr.views.text.contextactions

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import javafx.collections.transformation.FilteredList
import javafx.scene.control.MenuItem
import net.ladstatt.util.log.TinyLog

/**
 * Filters out all entries before the given current log entry and updates the current position.
 *
 * This filter will be reset if any other filter is changed in the filters menu bar.
 *
 * @param mutLogFileSettings     reference to global settings
 * @param currentEntry           current entry
 * @param filteredList           filtered list
 * @param scrollToActiveLogEntry function to scroll to current active log entry
 */
class IgnoreAboveMenuItem(mutLogFileSettings: MutLogFileSettings
                          , currentEntry: LogEntry
                          , filteredList: FilteredList[LogEntry]
                          , scrollToActiveLogEntry: () => Unit) extends MenuItem("Ignore entries above") with TinyLog:
  setOnAction(_ => {
    val currPredicate = filteredList.getPredicate
    filteredList.setPredicate((entry: LogEntry) => currPredicate.test(entry) && currentEntry.lineNumber <= entry.lineNumber)
    mutLogFileSettings.setSelectedLineNumber(currentEntry.lineNumber)
    scrollToActiveLogEntry()
  })

