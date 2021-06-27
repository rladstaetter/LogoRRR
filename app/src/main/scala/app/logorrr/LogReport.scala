package app.logorrr

import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ObservableList}
import app.util.{CanLog, JfxUtils}
import org.apache.commons.io.input.Tailer

import java.nio.file.{Files, Path}
import java.util
import java.util.stream.Collectors
import scala.language.postfixOps


/** Abstraction for a log file */
object LogReport extends CanLog {

  def apply(logFile: Path): LogReport = {
    val value = Files.readAllLines(logFile).stream().map(l => LogEntry(l))
    val entries = value.collect(Collectors.toList[LogEntry]())
    logTrace(s"Read ${entries.size} lines ... ")
    new LogReport(logFile, FXCollections.observableList(entries))
  }

  def indexOf(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): Int = y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth

}

case class LogReport(path: Path
                     , entries: ObservableList[LogEntry]) {

  val lengthProperty = new SimpleIntegerProperty(entries.size())
  val titleProperty = new SimpleStringProperty(computeTabName)

  val tailer = new Tailer(path.toFile, new LTailerListener(entries), 1000, true)

  /** start observing log file for changes */
  def start(): Unit = new Thread(tailer).start()

  /** stop observing changes */
  def stop(): Unit = tailer.stop()

  private def computeTabName = path.getFileName.toString + " (" + entries.size + " entries)"

  // if anything changes in entries list:
  //
  // - recompute title name
  // - recompute total size
  //
  entries.addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      JfxUtils.execOnUiThread {
        titleProperty.set(computeTabName)
        lengthProperty.set(entries.size)
      }
    }
  })


}


