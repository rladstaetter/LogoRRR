package net.ladstatt.logboard.views

import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.control.{Label, ScrollPane}
import javafx.scene.image.{ImageView, PixelWriter, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import net.ladstatt.logboard.{LogEntry, LogReport, LogSeverity}

import scala.jdk.CollectionConverters._

class LogVisualView(entries: java.util.List[LogEntry]
                    , squareWidth: Int
                    , canvasWidth: Int) extends BorderPane {

  val currentCanvasWidthProperty = new SimpleIntegerProperty(canvasWidth)

  def getCanvasWidth(): Int = currentCanvasWidthProperty.get()

  def setCanvasWidth(canvasWidth: Int): Unit = currentCanvasWidthProperty.set(canvasWidth)

  val selectedEntry = new SimpleObjectProperty[LogEntry]()

  def setSelectedEntry(e: LogEntry): Unit = selectedEntry.set(e)

  def getSelectedEntry(): LogEntry = selectedEntry.get()

  /** will be set by mouse clicks / movements */
  val selectedIndexProperty = new SimpleIntegerProperty()

  def setSelectedIndex(i: Int): Unit = selectedIndexProperty.set(i)

  def getSelectedIndex(): Int = selectedIndexProperty.get()

  selectedEntry.addListener(new ChangeListener[LogEntry] {
    override def changed(observableValue: ObservableValue[_ <: LogEntry], t: LogEntry, entry: LogEntry): Unit = {
      label.setBackground(LogSeverity.backgrounds.get(entry.severity))
      label.setText(s"L: ${getSelectedIndex() + 1} ${entry.value}")
    }
  })


  val label = {
    val l = new Label("")
    l.setPrefWidth(canvasWidth)
    l.setStyle(s"-fx-text-fill: white; -fx-font-size: 20px;")
    l
  }

  /** responsible for determining current logevent */
  val mouseEventHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = LogReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, getCanvasWidth())
      val entry = entries.get(index)
      setSelectedIndex(index)
      setSelectedEntry(entry)
    }
  }

  val view = {
    val iv = new ImageView(paint(entries, squareWidth, canvasWidth))
    iv.setOnMouseMoved(mouseEventHandler)
    iv
  }
  setBottom(label)
  setCenter(new ScrollPane(view))

  def doRepaint(sWidth: Int, cWidth: Int): Unit = {
    setCanvasWidth(cWidth)
    view.setImage(paint(entries, sWidth, cWidth))
    label.setPrefWidth(cWidth)
  }

  def paint(entries: java.util.List[LogEntry], squareWidth: Int, canvasWidth: Int): WritableImage = {
    val numberCols = canvasWidth / squareWidth
    val numRows = entries.size() / numberCols
    val height = squareWidth * numRows
    val wi = new WritableImage(canvasWidth + squareWidth, height + squareWidth)
    val pw = wi.getPixelWriter

    for ((e, i) <- entries.asScala.zipWithIndex) {
      paintSquare(pw, (i % numberCols) * squareWidth, (i / numberCols) * squareWidth, squareWidth.toInt, e.severity.color)
    }
    wi
  }

  def paintSquare(pw: PixelWriter, u: Int, v: Int, length: Int, c: Color): PixelWriter = {
    for {x <- u until (u + length - 1)
         y <- v until (v + length - 1)} {
      pw.setColor(x, y, c)
    }
    pw
  }

}
