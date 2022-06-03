package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogFileSettings
import app.logorrr.util.CanLog
import app.logorrr.views.LogFileTab
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.ListHasAsScala


/**
 * Main UI element, all other gui elements are in some way children of this Borderpane
 *
 * @param initialSquareWidth width of squares to paint in visual view
 */
class LogoRRRMainBorderPane extends BorderPane with CanLog {

  val logViewTabPane = new LogoRRRMainTabPane()

  setCenter(logViewTabPane)

  def init(): Unit = {
    /** needed to activate drag'n drop */
    setOnDragOver((event: DragEvent) => {
      if (event.getDragboard.hasFiles) {
        event.acceptTransferModes(TransferMode.ANY: _*)
      }
    })

    /** try to interpret dropped element as log file, activate view */
    setOnDragDropped((event: DragEvent) => {
      for (f <- event.getDragboard.getFiles.asScala) {
        val path = f.toPath
        if (Files.isDirectory(path)) {
          Files.list(path).filter((p: Path) => Files.isRegularFile(p)).forEach((t: Path) => dropLogFile(t))
        } else dropLogFile(path)
      }
    })

    logViewTabPane.init()
  }

  private def dropLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString
    if (Files.exists(path)) {
      if (!contains(pathAsString)) {
        val logFileSettings = LogFileSettings(path)
        LogoRRRGlobals.updateLogFile(logFileSettings)
        addLogFileTab(LogFileTab(logFileSettings.pathAsString, logFileSettings.readEntries()))
        selectLog(pathAsString)
      } else {
        logWarn(s"$pathAsString is already opened ...")
      }
    } else {
      logWarn(s"$pathAsString does not exist.")
    }
  }

  def shutdown(): Unit = logViewTabPane.shutdown()

  /** Adds a new logfile to display */
  def addLogFileTab(tab: LogFileTab): Unit = logViewTabPane.add(tab)

  def selectLog(path: String): Unit = logViewTabPane.selectLog(path)

  /** select log file which is last in tabPane */
  def selectLastLogFile(): Unit = logViewTabPane.selectLastLogFile()

  def contains(path: String): Boolean = logViewTabPane.contains(path)


}



