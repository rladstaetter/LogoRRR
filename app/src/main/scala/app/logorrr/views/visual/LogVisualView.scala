package app.logorrr.views.visual

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.scene.layout.BorderPane

import scala.collection.mutable

class LogVisualView(entries: mutable.Buffer[LogEntry]
                    , canvasWidth: Int)
  extends BorderPane with CanLog {

  require(canvasWidth > 0, "canvasWidth must be greater than 0")

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  /** will be set by mouse clicks / movements */
  val selectedIndexProperty = new SimpleIntegerProperty()

  val sisp = new SquareImageScrollPane(entries
    , selectedIndexProperty
    , selectedEntryProperty
    , canvasWidth)
  setCenter(sisp)

  def repaint( cWidth: Int): Unit = timeR(sisp.repaint( cWidth), "Repaint")


}
