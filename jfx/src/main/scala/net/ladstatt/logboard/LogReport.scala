package net.ladstatt.logboard

import net.ladstatt.util.CanLog

import java.nio.file.{Files, Path}
import java.util
import java.util.function.Predicate
import java.util.stream.Collectors
import scala.language.postfixOps

/** Abstraction for a log file */
object LogReport extends CanLog {

  def apply(logFile: Path): LogReport = {
    val value = Files.readAllLines(logFile).stream().map(LogEntry.apply)
    val entries = value.collect(Collectors.toList[LogEntry]())
    logTrace(s"Read ${entries.size} lines ... ")
    new LogReport(logFile.getFileName.toString, entries)
  }

  private def mkPredicate(c: LogSeverity): Predicate[LogEntry] = {
    (t: LogEntry) => t.severity == c
  }

  val infoP: Predicate[LogEntry] = mkPredicate(LogSeverity.Info)
  val warningP: Predicate[LogEntry] = mkPredicate(LogSeverity.Warning)
  val traceP: Predicate[LogEntry] = mkPredicate(LogSeverity.Trace)
  val severeP: Predicate[LogEntry] = mkPredicate(LogSeverity.Severe)
  val otherP: Predicate[LogEntry] = mkPredicate(LogSeverity.Other)

  def indexOf(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): Int = y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth

}

case class LogReport(name: String, entries: java.util.List[LogEntry]) {

  val occurences = {
    val occs = new util.HashMap[LogSeverity, Long]()
    occs.put(LogSeverity.Info, entries.stream.filter(LogReport.infoP).count())
    occs.put(LogSeverity.Warning, entries.stream.filter(LogReport.warningP).count())
    occs.put(LogSeverity.Trace, entries.stream.filter(LogReport.traceP).count())
    occs.put(LogSeverity.Severe, entries.stream.filter(LogReport.severeP).count())
    occs.put(LogSeverity.Other, entries.stream.filter(LogReport.otherP).count())
    occs
  }


  def getEntryAt(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): LogEntry = {
    entries.get(y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth)
  }


}