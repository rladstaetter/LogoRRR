package net.ladstatt.logboard

import java.nio.file.{Files, Path}
import java.text.DecimalFormat
import java.util
import java.util.function.Predicate
import java.util.stream.Collectors
import scala.language.postfixOps


object LogReport {

  def apply(logFile: Path): LogReport = {
    val value = Files.readAllLines(logFile).stream().map(LogEntry.apply)
    new LogReport(logFile.getFileName.toString, value.collect(Collectors.toList[LogEntry]()))
  }

  private def mkPredicate(c: LogSeverity): Predicate[LogEntry] = {
    (t: LogEntry) => t.severity == c
  }

  val infoP: Predicate[LogEntry] = mkPredicate(LogSeverity.Info)
  val warningP: Predicate[LogEntry] = mkPredicate(LogSeverity.Warning)
  val traceP: Predicate[LogEntry] = mkPredicate(LogSeverity.Trace)
  val severeP: Predicate[LogEntry] = mkPredicate(LogSeverity.Severe)
  val otherP: Predicate[LogEntry] = mkPredicate(LogSeverity.Other)

  val percentFormatter = new DecimalFormat("#.##")
}

case class LogReport(name: String, entries: java.util.List[LogEntry]) {


  def percentAsString(ls: LogSeverity): String = {
    LogReport.percentFormatter.format((100 * occurences.get(ls).toDouble) / entries.size().toDouble)
  }

  val occurences = {
    val occs = new util.HashMap[LogSeverity, Long]()
    occs.put(LogSeverity.Info, entries.stream.filter(LogReport.infoP).count())
    occs.put(LogSeverity.Warning, entries.stream.filter(LogReport.warningP).count())
    occs.put(LogSeverity.Trace, entries.stream.filter(LogReport.traceP).count())
    occs.put(LogSeverity.Severe, entries.stream.filter(LogReport.severeP).count())
    occs.put(LogSeverity.Other, entries.stream.filter(LogReport.otherP).count())
    occs
  }


}