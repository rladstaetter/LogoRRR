package app.logorrr.views.main

import app.logorrr.conf.{LogoRRRGlobals, SettingsIO, StageSettings}
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.CanLog
import app.logorrr.views.{Filter, Fltr, LogoRRRMainTabPane}
import javafx.application.HostServices
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.ListHasAsScala


/**
 * Main UI element, all other gui elements are in some way children of this Borderpane
 *
 * @param initialSquareWidth width of squares to paint in visual view
 */
class LogoRRRMainBorderPane(reInitMenuBarFn: => Unit)
  extends BorderPane with CanLog {

  val sceneWidthProperty = new SimpleIntegerProperty(LogoRRRGlobals.getStageWidth())

  val logViewTabPane = LogoRRRMainTabPane(this, reInitMenuBarFn)

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
        val pathAsString = path.toAbsolutePath.toString
        if (Files.exists(path)) {
          if (!contains(pathAsString)) {
            val logFileSettings = LogFileSettings(path)
            LogoRRRGlobals.updateLogFile(logFileSettings)
            reInitMenuBarFn
            setLogEntries(logFileSettings.pathAsString,logFileSettings.readEntries())
            selectLog(pathAsString)
          } else {
            logWarn(s"$pathAsString is already opened ...")
          }
        } else {
          logWarn(s"$pathAsString does not exist.")
        }
      }
    })

    logViewTabPane.init()
  }

  def shutdown(): Unit = logViewTabPane.shutdown()

  /** select log file which is last in tabPane */
  def selectLastLogFile(): Unit = logViewTabPane.selectLastLogFile()

  def selectLog(path: String): Unit = logViewTabPane.selectLog(path)

  /** Adds a new logfile to display */
  def setLogEntries(pathAsString: String, logEntries: ObservableList[LogEntry]): Unit = {
    logViewTabPane.add(pathAsString, logEntries)
  }

  /**
   * replaces existing log file tab with given one, if it does not exist yet create one.
   */
  def updateLogFileSettings(logFileSettings: LogFileSettings): Unit = {
    ???
  }

  def contains(path: String): Boolean = logViewTabPane.contains(path)


}



