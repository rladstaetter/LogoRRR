package net.ladstatt.logboard

import javafx.scene.image.{PixelWriter, WritableImage}
import javafx.scene.paint.Color
import net.ladstatt.util.CanLog

import java.nio.file.{Files, Path}
import java.text.DecimalFormat
import java.util
import java.util.function.Predicate
import java.util.stream.Collectors
import scala.jdk.CollectionConverters._
import scala.language.postfixOps


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

  def paintSquare(pw: PixelWriter, u: Int, v: Int, length: Int, c: Color): PixelWriter = {
    for {x <- u until (u + length - 1)
         y <- v until (v + length - 1)} {
      pw.setColor(x, y, c)
    }
    pw
  }

  def paint(squareWidth: Int, canvasWidth: Int): WritableImage = {
    val numberCols = canvasWidth / squareWidth
    val numRows = entries.size() / numberCols
    val height = squareWidth * numRows
    val wi = new WritableImage(canvasWidth + squareWidth, height + squareWidth)
    val pw = wi.getPixelWriter

    for ((e, i) <- entries.asScala.zipWithIndex) {
      paintSquare(pw, (i % numberCols) * squareWidth, (i / numberCols) * squareWidth, squareWidth.toInt, e.severity.color)
    }
    wi
  }

  def indexOf(x: Int, y: Int, squareWidth: Int, canvasWidth: Int) : Int = y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth

  def getEntryAt(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): LogEntry = {
    entries.get(y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth)
  }


}