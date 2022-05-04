package app.logorrr.views.visual

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.block.BlockViewPane
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane

class LogVisualView(pathAsString: String, entries: ObservableList[LogEntry], canvasWidth: Int)
  extends BorderPane with CanLog {

  def repaint() = blockViewPane.repaint()


  //  require(canvasWidth > 0, "canvasWidth must be greater than 0")

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  val blockViewPane = new BlockViewPane[LogEntry]
  blockViewPane.selectedElemProperty.addListener(new ChangeListener[LogEntry] {
    override def changed(observable: ObservableValue[_ <: LogEntry], oldValue: LogEntry, newValue: LogEntry): Unit = {
      LogoRRRGlobals.getLogFileSettings(pathAsString).setSelectedIndex(newValue.lineNumber)
    }
  })
  selectedEntryProperty.bind(blockViewPane.selectedElemProperty)
  blockViewPane.setCanvasWidth(canvasWidth)
  blockViewPane.setBlockSize(8)
  blockViewPane.setEntries(entries)

  setCenter(blockViewPane)


}
