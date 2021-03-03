package net.ladstatt.logboard




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

case class LogEntry(value: String, severity: LogSeverity) {

}
