package net.ladstatt.logboard

import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration

case class LogEntry(entry: String) {
  val color: Color = {
    if (entry.contains("SEVERE")) {
      Color.RED
    } else if (entry.contains("WARNING")) {
      Color.YELLOW
    } else if (entry.contains("FINEST")) {
      Color.GREY
    } else Color.GREEN
  }

  val rectangle: Rectangle = new Rectangle(5, 5, color)

  val someTooltip: Option[Tooltip] = {
    if (color == Color.RED) {
      val tt = new Tooltip(entry)
      tt.setShowDelay(new Duration(100))
      Tooltip.install(rectangle, tt)
      Option(tt)
    } else None
  }
}
