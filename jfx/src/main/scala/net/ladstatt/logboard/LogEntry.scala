package net.ladstatt.logboard

import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration

trait LogEntry {

  def value: String

  def rectangle: Rectangle

  def someTooltip : Option[Tooltip] = None
}

class InfoLogEntry(val value: String, val rectangle: Rectangle) extends LogEntry

class WarningLogEntry(val value: String, val rectangle: Rectangle) extends LogEntry

class TraceLogEntry(val value: String, val rectangle: Rectangle) extends LogEntry

object SevereLogEntry {
    val duration = new Duration(100)

}
class SevereLogEntry(val value: String, val rectangle: Rectangle) extends LogEntry {
  override val someTooltip: Option[Tooltip] = {
    val tt = new Tooltip(value)
    tt.setShowDelay(SevereLogEntry.duration)
    Tooltip.install(rectangle, tt)
    Option(tt)
  }
}

class UnknownLogEntry(val value: String, val rectangle: Rectangle) extends LogEntry

object LogEntry {

  def apply(line: String): LogEntry = {
    if (line.contains("SEVERE")) {
      new SevereLogEntry(line, new Rectangle(5, 5, Color.RED))
    } else if (line.contains("WARNING")) {
      new WarningLogEntry(line, new Rectangle(5, 5, Color.YELLOW))
    } else if (line.contains("FINEST")) {
      new TraceLogEntry(line, new Rectangle(5, 5, Color.GREY))
    } else if (line.contains("INFO")) {
      new InfoLogEntry(line, new Rectangle(5, 5, Color.GREEN))
    } else new UnknownLogEntry(line, new Rectangle(5, 5, Color.BLUE))
  }

}

