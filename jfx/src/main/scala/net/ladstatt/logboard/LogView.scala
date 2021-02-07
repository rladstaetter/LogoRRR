package net.ladstatt.logboard

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control._
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii}
import net.ladstatt.util.CanLog


object LogView {

  def apply(logReport: LogReport, squareWidth: Int, canvasWidth: Int): LogView = {
    val rt = new LogView(logReport, squareWidth, canvasWidth)
    rt
  }

}

class VisualView(logReport: LogReport, squareWidth: Int, canvasWidth: Int) extends Tab("Visual View") {

  val bp = new BorderPane()
  val label = new Label("")

  val view = {
    val iv = new ImageView(logReport.paint(squareWidth, canvasWidth))
    iv.setOnMouseMoved(new EventHandler[MouseEvent]() {
      override def handle(me: MouseEvent): Unit = {
        val index = logReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, canvasWidth)
        val entry = logReport.entries.get(index)
        label.setText(s"L: $index ${entry.value}")
        label.setPrefWidth(canvasWidth)
        label.setBackground(new Background(new BackgroundFill(entry.severity.color, new CornerRadii(5.0), new Insets(-5.0))))
        label.setStyle(s"-fx-text-fill: white; -fx-font-size: 20px;")
      }
    })
    iv
  }
  bp.setBottom(label)
  bp.setCenter(new ScrollPane(view))
  setContent(bp)

  def doRepaint(sWidth: Int, cWidth: Int): Unit = {
    view.setImage(logReport.paint(sWidth, cWidth))
  }
}


class LogView(logReport: LogReport
              , squareWidth: Int
              , canvasWidth: Int) extends Tab(logReport.name + " (" + logReport.entries.size() + " entries)") with CanLog {

  private val tabPane = new TabPane()
  private val logVisualView = new VisualView(logReport, squareWidth, canvasWidth)
  private val logTextView = new LogTextView(logReport)
  tabPane.getTabs.addAll(logVisualView, logTextView)
  setContent(tabPane)

  /**
   * initially, report tab will be painted when added. per default the newly added report will be painted once.
   * repaint property will be set to true if the report tab itself is not selected and the width of the application
   * changes. the tab will be repainted when selected.
   * */
  val repaintProperty = new SimpleBooleanProperty(false)

  def setRepaint(repaint: Boolean): Unit = repaintProperty.set(repaint)

  def getRepaint(): Boolean = repaintProperty.get()


  def doRepaint(sWidth: Int, cWidth: Int): Unit = {
    logTextView.doRepaint(sWidth, cWidth)
    logVisualView.doRepaint(sWidth, cWidth)
  }
}