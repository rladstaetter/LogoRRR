package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import javafx.beans.binding.Bindings
import javafx.collections.ObservableList
import javafx.scene.control.Tooltip
import javafx.util.Duration

/**
 * Displays meta information of given file
 */
class LogFileTabToolTip(fileId: FileId
                        , logEntries: ObservableList[LogEntry]) extends Tooltip:
  this.setShowDelay(Duration.millis(300))
  this.textProperty.bind(Bindings.concat(fileId.absolutePathAsString, "\n", Bindings.size(logEntries).asString, " lines"))
