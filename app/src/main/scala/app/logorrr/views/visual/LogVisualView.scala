package app.logorrr.views.visual

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.block.BlockViewPane
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane

class LogVisualView(entries: ObservableList[LogEntry], canvasWidth: Int)
  extends BorderPane with CanLog {

  def repaint() = blockViewPane.repaint()


  //  require(canvasWidth > 0, "canvasWidth must be greater than 0")

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  val blockViewPane = new BlockViewPane[LogEntry]
  selectedEntryProperty.bind(blockViewPane.selectedElemProperty)
  blockViewPane.setCanvasWidth(canvasWidth)
  blockViewPane.setBlockSize(8)
  blockViewPane.setEntries(entries)

  setCenter(blockViewPane)


}
