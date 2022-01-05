package app.logorrr.views.visual

import app.logorrr.model.LogEntry
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.scene.layout.BorderPane
import app.logorrr.util.CanLog

import scala.collection.mutable

class LogVisualView(entries: mutable.Buffer[LogEntry]
                    , canvasWidth: Int
                    , squareWidth: Int) extends BorderPane with CanLog {

  require(canvasWidth > 0, "squareWidth must be greater than 0")
  require(squareWidth > 0, "canvasWidth must be greater than 0")

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  /** will be set by mouse clicks / movements */
  val selectedIndexProperty = new SimpleIntegerProperty()

  val sisp = new SquareImageScrollPane(entries
    , selectedIndexProperty
    , selectedEntryProperty
    , squareWidth
    , canvasWidth)
  setCenter(sisp)

  def repaint(sWidth: Int, cWidth: Int): Unit = timeR({
    sisp.repaint(sWidth, cWidth)
  }, "Repaint")


}
