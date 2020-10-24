package net.ladstatt.logboard

import java.net.URL
import java.nio.file.{Files, Path}
import java.util.ResourceBundle

import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Tooltip
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.FlowPane

import scala.jdk.CollectionConverters._


class LogboardController extends Initializable {

  val logEntries = new SimpleObjectProperty[Seq[LogEntry]]()

  @FXML var flowPane: FlowPane = _


  def clearLogEntries(): Unit = {
    Option(logEntries.get()) match {
      case Some(entries) =>
        for (e <- entries) {
          e.someTooltip.foreach(t => Tooltip.uninstall(flowPane, t))
        }
      case None =>
    }
    flowPane.getChildren.clear()
  }

  def setLogEntries(entries: Seq[LogEntry]): Unit = {
    logEntries.set(entries)
    flowPane.getChildren.addAll(logEntries.get.map(_.rectangle): _*)
  }

  @FXML
  def handleDragOver(event: DragEvent): Unit = {
    if (event.getDragboard.hasFiles) {
      event.acceptTransferModes(TransferMode.ANY: _*)
    }
  }

  @FXML
  def handleDrop(event: DragEvent): Unit = {
    val logFile: Path = event.getDragboard.getFiles.get(0).toPath
    if (Files.isReadable(logFile) && Files.isRegularFile(logFile)) {
      clearLogEntries()
      setLogEntries(Files.readAllLines(logFile).asScala.map(e => LogEntry(e)).toSeq)
    }
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {}

}
