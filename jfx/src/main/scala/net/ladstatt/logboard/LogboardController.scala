package net.ladstatt.logboard

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.image.ImageView
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.FlowPane
import javafx.scene.shape.Rectangle
import javafx.stage.FileChooser
import net.ladstatt.util.CanLog

import java.net.URL
import java.nio.file.{Files, Path}
import java.util.ResourceBundle
import java.util.stream.Collectors



class LogboardController extends Initializable with CanLog {

  val logEntries = new SimpleListProperty[LogEntry](FXCollections.observableArrayList())

  @FXML var flowPane: FlowPane = _
  @FXML var selectFileButton: Button = _
  @FXML var logElementCanvas: ImageView = _

  def clearLogEntries(): Unit = {
    Option(logEntries.get()) match {
      case Some(entries) =>
        entries
          .filtered((t: LogEntry) => t.severity == LogSeverity.Severe)
          .forEach(e => {
            e.someTooltip match {
              case Some(t) =>
                logTrace("Clearing element ...")
                Tooltip.uninstall(flowPane, t)
              case None =>
                logTrace("Clearing element ...")
            }
          })
      case None =>
    }
    logTrace("Clearing " + flowPane.getChildren.size() + " elements ... start")
    //    flowPane.getChildren.clear()
    logTrace("Clearing " + flowPane.getChildren.size() + " elements ... stop")
  }

  def setLogEntries(entries: java.util.List[LogEntry]): Unit = {
    logEntries.addAll(entries)


    flowPane.getChildren.setAll(entries.stream.map(_.rectangle).collect(Collectors.toList[Rectangle]()))
    /*
    entries.forEach(e => {
      flowPane.getChildren.add(e.rectangle)
    })
*/
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
      timeR(clearLogEntries(), "Clearing log entries ...")
      timeR(setLogFile(logFile), "Setting new log entries ..")
    }
  }


  private def setLogFile(logFile: Path): Unit = {
    setLogEntries(LogReport(logFile).entries)
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



