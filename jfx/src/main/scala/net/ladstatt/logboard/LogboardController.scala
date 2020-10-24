package net.ladstatt.logboard

import java.net.URL
import java.nio.file.{Files, Path}
import java.util.ResourceBundle
import java.util.stream.Collectors

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser


class LogboardController extends Initializable {

  val logEntries = new SimpleListProperty[LogEntry](FXCollections.observableArrayList())

  @FXML var flowPane: FlowPane = _
  @FXML var selectFileButton: Button = _

  def clearLogEntries(): Unit = {
    Option(logEntries.get()) match {
      case Some(entries) =>
        entries.filtered {
          case _: SevereLogEntry => true
          case _ => false
        }.forEach(e => {
          e.someTooltip match {
            case Some(t) => Tooltip.uninstall(flowPane, t)
            case None =>
          }
        })
      case None =>
    }
    flowPane.getChildren.clear()
  }

  def setLogEntries(entries: java.util.List[LogEntry]): Unit = {
    logEntries.addAll(entries)

    entries.forEach(e => {
      flowPane.getChildren.add(e.rectangle)
    })

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
      setLogFile(logFile)
    }
  }

  private def setLogFile(logFile: Path): Unit = {
    val entries = Files.readAllLines(logFile).parallelStream().map(LogEntry.apply).collect(Collectors.toList())
    println(entries.size)
    setLogEntries(entries)
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    selectFileButton.setOnAction((t: ActionEvent) => {
      val c = new FileChooser()
      c.setTitle("Select log file")
      Option(c.showOpenDialog(null)) match {
        case Some(file) => setLogFile(file.toPath)
        case None =>
      }
    })

  }

}
