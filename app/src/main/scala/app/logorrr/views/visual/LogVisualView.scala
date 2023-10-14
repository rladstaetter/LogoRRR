package app.logorrr.views.visual

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.block.BlockViewPane
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane

class LogVisualView(selectedLineNumberProperty: SimpleIntegerProperty
                    , filtersProperty: SimpleListProperty[Filter]
                    , entries: ObservableList[LogEntry]
                    , canvasWidth: Int)
  extends BorderPane with CanLog {

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  val blockViewPane = new BlockViewPane(selectedLineNumberProperty)

  init()

  def init(): Unit = {
    // init bindings
    blockViewPane.filtersProperty.bind(filtersProperty)
    selectedEntryProperty.bind(blockViewPane.selectedElemProperty)


    blockViewPane.setCanvasWidth(canvasWidth)
    blockViewPane.setBlockSize(8)
    blockViewPane.getEntriesSize()
    blockViewPane.setEntries(entries)

    setCenter(blockViewPane)
  }


  def shutdown(): Unit = {
    // shutdown inner class
    blockViewPane.shutdown()

    // unbinds
    blockViewPane.filtersProperty.unbind()
    selectedEntryProperty.unbind()
    // remove listeners ... there are none

  }


  def repaint(): Unit = blockViewPane.repaint()

  /**
   * makes sure that last line of block view is visible, useful for autoscroll feature
   */
  def scrollToEnd(): Unit = blockViewPane.scrollToEnd()


}
