package net.ladstatt.logboard

import javafx.scene.control.Tooltip
import javafx.scene.shape.Rectangle
import javafx.util.Duration


case class LogEntry(value: String, severity: LogSeverity) {

  def rectangle: Rectangle = new Rectangle(5, 5, severity.color)

  def someTooltip: Option[Tooltip] = {
    severity match {
      case LogSeverity.Severe =>
        val tt = new Tooltip(value)
        tt.setShowDelay(new Duration(100))
        Tooltip.install(rectangle, tt)
        Option(tt)
      case _ => None
    }

  }
}


object LogEntry {

  def apply(line: String): LogEntry = {
    if (line.contains("SEVERE")) {
      new LogEntry(line, LogSeverity.Severe)
    } else if (line.contains("WARNING")) {
      new LogEntry(line, LogSeverity.Warning)
    } else if (line.contains("FINEST")) {
      new LogEntry(line, LogSeverity.Trace)
    } else if (line.contains("INFO")) {
      new LogEntry(line, LogSeverity.Info)
    } else new LogEntry(line, LogSeverity.Other)
  }

}

