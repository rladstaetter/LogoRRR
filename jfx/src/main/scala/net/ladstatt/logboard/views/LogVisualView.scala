package net.ladstatt.logboard.views

import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.{Label, ScrollPane}
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii}
import net.ladstatt.logboard.{LogEntry, LogReport}

object LogVisualView {
  val radii = new CornerRadii(5.0)
  val insets = new Insets(-5.0)

}

class LogVisualView(logReport: LogReport
                    , squareWidth: Int
                    , canvasWidth: Int) extends BorderPane {

  val currentCanvasWidthProperty = new SimpleIntegerProperty(canvasWidth)

  def getCanvasWidth(): Int = currentCanvasWidthProperty.get()

  def setCanvasWidth(canvasWidth: Int): Unit = currentCanvasWidthProperty.set(canvasWidth)

  val selectedEntry = new SimpleObjectProperty[LogEntry]()

  def setSelectedEntry(e: LogEntry): Unit = selectedEntry.set(e)

  def getSelectedEntry(): LogEntry = selectedEntry.get()

  val selectedIndexProperty = new SimpleIntegerProperty()

  def setSelectedIndex(i: Int): Unit = selectedIndexProperty.set(i)

  def getSelectedIndex(): Int = selectedIndexProperty.get()

  selectedEntry.addListener(new ChangeListener[LogEntry] {
    override def changed(observableValue: ObservableValue[_ <: LogEntry], t: LogEntry, entry: LogEntry): Unit = {
      label.setBackground(new Background(new BackgroundFill(entry.severity.color, LogVisualView.radii, LogVisualView.insets)))
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
      val index = logReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, getCanvasWidth())
      val entry = logReport.entries.get(index)
      setSelectedIndex(index)
      setSelectedEntry(entry)
    }
  }

  val view = {
    val iv = new ImageView(logReport.paint(squareWidth, canvasWidth))
    iv.setOnMouseMoved(mouseEventHandler)
    iv
  }
  setBottom(label)
  setCenter(new ScrollPane(view))

  def doRepaint(sWidth: Int, cWidth: Int): Unit = {
    setCanvasWidth(cWidth)
    view.setImage(logReport.paint(sWidth, cWidth))
    label.setPrefWidth(cWidth)
  }
}
