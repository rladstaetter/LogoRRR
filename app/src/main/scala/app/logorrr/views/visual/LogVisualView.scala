package app.logorrr.views.visual

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.block.BlockViewPane
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane

class LogVisualView(pathAsString: String, entries: ObservableList[LogEntry], canvasWidth: Int)
  extends BorderPane with CanLog {

  def repaint(): Unit = blockViewPane.repaint()

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  val blockViewPane = new BlockViewPane(pathAsString)
  blockViewPane.filtersProperty.bind(LogoRRRGlobals.getLogFileSettings(pathAsString).filtersProperty)
  selectedEntryProperty.bind(blockViewPane.selectedElemProperty)
  blockViewPane.setCanvasWidth(canvasWidth)
  blockViewPane.setBlockSize(8)
  blockViewPane.setEntries(entries)

  setCenter(blockViewPane)


  /**
   * makes sure that last line of block view is visible, useful for autoscroll feature
   */
  def scrollToEnd(): Unit = {
    blockViewPane.scrollToEnd()
  }


}
