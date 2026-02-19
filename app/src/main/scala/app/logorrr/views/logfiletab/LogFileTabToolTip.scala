package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import javafx.beans.binding.Bindings
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.scene.control.Tooltip
import javafx.util.Duration


class LogFileTabToolTip extends Tooltip:

  setShowDelay(Duration.millis(300))

  def init(fileIdProperty: ObjectPropertyBase[FileId], entries: ObservableList[LogEntry]): Unit =
    textProperty.bind(
      Bindings.concat(
        fileIdProperty.map((fileId: FileId) => fileId.absolutePathAsString)
        , "\n"
        , Bindings.size(entries).asString
        , " lines")
    )

  def unbind(): Unit = textProperty().unbind()