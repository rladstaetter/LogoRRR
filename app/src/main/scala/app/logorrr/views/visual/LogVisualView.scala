package app.logorrr.views.visual

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.block.{BlockView, BlockViewPane}
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.collections.transformation.FilteredList
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color

import java.util.function.Predicate
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class LogVisualView(entries: ObservableList[LogEntry], canvasWidth: Int)
  extends BorderPane with CanLog {

  //  require(canvasWidth > 0, "canvasWidth must be greater than 0")

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()



  import scala.jdk.CollectionConverters._

  val blockViewPane = new BlockViewPane[LogEntry]
  selectedEntryProperty.bind(blockViewPane.selectedElemProperty)
  blockViewPane.setCanvasWidth(canvasWidth)
  blockViewPane.setBlockSize(8)
  blockViewPane.setEntries(entries)

  setCenter(blockViewPane)

  def repaint(cWidth: Int): Unit = {
    // blockViewPane.setWidth(cWidth)
    //    timeR(sisp.repaint(cWidth), "Repaint")
  }


}
