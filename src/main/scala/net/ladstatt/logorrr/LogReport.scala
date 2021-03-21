package net.ladstatt.logorrr

import net.ladstatt.util.CanLog

import java.nio.file.{Files, Path}
import java.util.stream.Collectors
import scala.collection.mutable
import scala.language.postfixOps

/** Abstraction for a log file */
object LogReport extends CanLog {

  import scala.jdk.CollectionConverters._

  def apply(logFile: Path): LogReport = {
    val value = Files.readAllLines(logFile).stream().map(LogEntry.apply)
    val entries = value.collect(Collectors.toList[LogEntry]())
    logTrace(s"Read ${entries.size} lines ... ")
    new LogReport(logFile, entries.asScala)
  }

  def indexOf(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): Int = y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth

}

case class LogReport(path: Path
                     , entries: mutable.Buffer[LogEntry]) {

  val name = path.getFileName.toString


  def getEntryAt(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): LogEntry = {
    entries(y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth)
  }


}