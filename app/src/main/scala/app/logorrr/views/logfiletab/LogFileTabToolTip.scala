package app.logorrr.views.logfiletab

import app.logorrr.model.LogEntry
import javafx.beans.binding.Bindings
import javafx.collections.ObservableList
import javafx.scene.control.Tooltip
import javafx.util.Duration

/**
 * Displays meta information of given file
 */
class LogFileTabToolTip(pathAsString: String
                        , logEntries: ObservableList[LogEntry]) extends Tooltip {
  this.setShowDelay(Duration.millis(100))
  this.textProperty.bind(Bindings.concat(pathAsString, "\n", Bindings.size(logEntries).asString, " lines"))
}
