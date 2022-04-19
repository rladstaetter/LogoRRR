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

  /** will be set by mouse clicks / movements */
  val selectedIndexProperty = {
    val sip = new SimpleIntegerProperty()
    sip.addListener(JfxUtils.onNew[Number](n => {
      println("ENTRIES: " + entries.size + ", n = " + n.intValue())
      Try(entries.filtered((t: LogEntry) => t.lineNumber == n.intValue()).get(0)) match {
        case Failure(exception) =>
         logException("could not lookup entries.", exception)
        case Success(value) =>
          selectedEntryProperty.set(value)
      }
    }))
    sip
  }


  import scala.jdk.CollectionConverters._

  val blockViewPane = new BlockViewPane[LogEntry]
  selectedIndexProperty.bind(blockViewPane.selectedIndexProperty)
  blockViewPane.setCanvasWidth(canvasWidth)
  blockViewPane.setBlockSize(8)
  blockViewPane.setEntries(entries)

  //  blockViewPane.setWidth(canvasWidth)
  /*
  val sisp = new SquareImageScrollPane(entries
    , selectedIndexProperty
    , selectedEntryProperty
    , canvasWidth)

   */
  setCenter(blockViewPane)

  def repaint(cWidth: Int): Unit = {
    // blockViewPane.setWidth(cWidth)
    //    timeR(sisp.repaint(cWidth), "Repaint")
  }


}
