package app.logorrr.views.search

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.autoscroll.AutoScrollCheckBox
import app.logorrr.views.ops.{ClearLogButton, CopyLogButton}
import javafx.beans.property.{ObjectPropertyBase, Property}
import javafx.collections.ObservableList
import javafx.scene.Node

class OtherItemsRegion:

  private val autoScrollCheckBox = new AutoScrollCheckBox
  private val clearLogButton = new ClearLogButton
  private val copySelectionButton = new CopyLogButton

  val items: Seq[Node] = Seq(autoScrollCheckBox, clearLogButton, copySelectionButton)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , autoScrollProperty: Property[java.lang.Boolean]
           , logEntries: ObservableList[LogEntry]
           , filteredList: ObservableList[LogEntry]): Unit = {
    autoScrollCheckBox.init(fileIdProperty, autoScrollProperty)
    clearLogButton.init(fileIdProperty, logEntries)
    copySelectionButton.init(fileIdProperty, filteredList)
  }

  def shutdown(autoScrollProperty: Property[java.lang.Boolean]
               , logEntries: ObservableList[LogEntry]
               , filteredList: ObservableList[LogEntry]): Unit =
    autoScrollCheckBox.shutdown(autoScrollProperty)
    clearLogButton.shutdown(logEntries)
    copySelectionButton.shutdown(filteredList)