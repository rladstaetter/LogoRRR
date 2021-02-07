package net.ladstatt.logboard.views

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.{Label, ScrollPane, Tab}
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii}
import net.ladstatt.logboard.LogReport

class LogVisualView(logReport: LogReport, squareWidth: Int, canvasWidth: Int) extends Tab("Visual View") {

  val bp = new BorderPane()
  val radii = new CornerRadii(5.0)
  val insets = new Insets(-5.0)

  val label = {
    val l = new Label("")
    l.setPrefWidth(canvasWidth)
    l.setStyle(s"-fx-text-fill: white; -fx-font-size: 20px;")
    l
  }

  val view = {
    val iv = new ImageView(logReport.paint(squareWidth, canvasWidth))
    iv.setOnMouseMoved(new EventHandler[MouseEvent]() {
      override def handle(me: MouseEvent): Unit = {
        val index = logReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, canvasWidth)
        val entry = logReport.entries.get(index)
        label.setBackground(new Background(new BackgroundFill(entry.severity.color, radii, insets)))
        label.setText(s"L: ${index + 1} ${entry.value}")
      }
    })
    iv
  }
  bp.setBottom(label)
  bp.setCenter(new ScrollPane(view))
  setContent(bp)

  def doRepaint(sWidth: Int, cWidth: Int): Unit = {
    view.setImage(logReport.paint(sWidth, cWidth))
    label.setPrefWidth(cWidth)
  }
}
