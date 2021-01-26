package net.ladstatt.logboard

import java.nio.file.{Files, Path}
import java.util.logging.{Formatter, LogRecord}
import java.util.stream.Collectors
import scala.language.postfixOps


object LogReport {


  def apply(logFile: Path): LogReport = {
    val value = Files.readAllLines(logFile).stream().map(LogEntry.apply)
    new LogReport(value.collect(Collectors.toList[LogEntry]()))
  }

/*
  def apply(logFile: Path): LogReport = timeR({
    new LogReport(Files.readAllLines(logFile).parallelStream().map(LogEntry.apply).collect(Collectors.toList[LogEntry]()))
  }, s"Loaded ${logFile.toAbsolutePath.toString}")
*/
}

case class LogReport(entries: java.util.List[LogEntry])