package net.ladstatt.logboard.views

import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import net.ladstatt.logboard.{LogEntry, LogReport}
import net.ladstatt.util.CanLog

import scala.collection.mutable


class LogVisualView(entries: mutable.Buffer[LogEntry]
                    , canvasWidth: Int
                    , squareWidth: Int) extends BorderPane with CanLog {

  require(canvasWidth > 0, "squareWidth must be greater than 0")
  require(squareWidth > 0, "canvasWidth must be greater than 0")

  val currentCanvasWidthProperty = new SimpleIntegerProperty(canvasWidth)

  def getCanvasWidth(): Int = currentCanvasWidthProperty.get()

  def setCanvasWidth(canvasWidth: Int): Unit = currentCanvasWidthProperty.set(canvasWidth)

  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  def setSelectedEntry(e: LogEntry): Unit = selectedEntryProperty.set(e)

  /** will be set by mouse clicks / movements */
  val selectedIndexProperty = new SimpleIntegerProperty()

  def setSelectedIndex(i: Int): Unit = selectedIndexProperty.set(i)

  /** responsible for determining current logevent */
  val mouseEventHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = LogReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, getCanvasWidth())
      val entry = entries(index)
      setSelectedIndex(index)
      setSelectedEntry(entry)
    }
  }

  val view = {
    val iv = SquareImageView(entries, squareWidth, canvasWidth)
    iv.setOnMouseMoved(mouseEventHandler)
    iv
  }

  setCenter(new ScrollPane(view))

  def doRepaint(sWidth: Int, cWidth: Int): Unit = {
    setCanvasWidth(cWidth)
    view.setImage(SquareImageView.paint(entries, sWidth, cWidth))
  }


}
