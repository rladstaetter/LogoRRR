package net.ladstatt.logboard

import java.net.URL
import java.util.ResourceBundle

import javafx.animation.AnimationTimer
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.util.Duration


object ShapeColors {
  val seq = Seq(Color.RED, Color.YELLOW, Color.GRAY, Color.GREEN)
}

class LogboardController extends Initializable {

  val logEntries = new SimpleObjectProperty[Vector[String]]()

  def setLogEntries(entries: Vector[String]): Unit = logEntries.set(entries)

  def getLogEntries(): Vector[String] = logEntries.get()

  @FXML var flowPane: FlowPane = _
  var currIndex: Int = 0

  def deduceColor(entry: String): Color = {
    if (entry.contains("SEVERE")) {
      Color.RED
    } else if (entry.contains("WARNING")) {
      Color.YELLOW
    } else if (entry.contains("FINEST")) {
      Color.GREY
    } else Color.GREEN
  }

  val animation = new AnimationTimer() {

    override def handle(l: Long): Unit = {
      if (currIndex < getLogEntries.size) {
        val entry = getLogEntries()(currIndex)
        println(entry)
        val color = deduceColor(entry)
        val r = new Circle(3, color)
        val tooltip = new Tooltip(entry)
        tooltip.setShowDelay(new Duration(100))
        Tooltip.install(r, tooltip)
        if (flowPane.getChildren.size > 1000) {
          flowPane.getChildren.clear()
        }
        flowPane.getChildren.add(r)
        currIndex = currIndex + 1
      } else {
        stop()
      }
    }
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    animation.start()
  }

}
