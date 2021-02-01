package net.ladstatt.logboard

import java.nio.file.{Files, Path}
import java.util.stream.Collectors
import scala.language.postfixOps


object LogReport {

  def apply(logFile: Path): LogReport = {
    val value = Files.readAllLines(logFile).stream().map(LogEntry.apply)
    new LogReport(logFile.getFileName.toString, value.collect(Collectors.toList[LogEntry]()))
  }

}

case class LogReport(name: String, entries: java.util.List[LogEntry])