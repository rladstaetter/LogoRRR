package app.logorrr.views.main

import app.logorrr.conf.{LogoRRRGlobals, SettingsIO, StageSettings}
import app.logorrr.model.LogFileSettings
import app.logorrr.util.CanLog
import app.logorrr.views.{Filter, Fltr, LogoRRRMainTabPane}
import javafx.application.HostServices
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.ListHasAsScala

object LogoRRRMainBorderPane {

  def apply(stageSettings: StageSettings
            , reInitMenuBarFn: => Unit): LogoRRRMainBorderPane = {
    new LogoRRRMainBorderPane(stageSettings.width, reInitMenuBarFn)
  }
}

/**
 * Main UI element, all other gui elements are in some way children of this Borderpane
 *
 * @param initialSceneWidth  initial width of scene
 * @param initialSquareWidth width of squares to paint in visual view
 */
class LogoRRRMainBorderPane(initialSceneWidth: Int
                            , reInitMenuBarFn: => Unit)
  extends BorderPane with CanLog {

  val sceneWidthProperty = new SimpleIntegerProperty(initialSceneWidth)

  val logViewTabPane = LogoRRRMainTabPane(this, reInitMenuBarFn)


  def init(): Unit = {
    setCenter(logViewTabPane)

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
        if (Files.exists(path)) {
          if (!contains(path)) {
            val logFileSettings = LogFileSettings(path)
            LogoRRRGlobals.updateLogFile(logFileSettings.pathAsString, logFileSettings)
            reInitMenuBarFn
            addLogFile(logFileSettings)
            selectLog(path)
          } else {
            logWarn(s"${path.toAbsolutePath.toString} is already opened ...")
          }
        } else {
          logWarn(s"${path.toAbsolutePath.toString} does not exist.")
        }
      }
    })

    logViewTabPane.init()
  }

  def shutdown(): Unit = logViewTabPane.shutdown()

  /** select log file which is last in tabPane */
  def selectLastLogFile(): Unit = logViewTabPane.selectLastLogFile()

  def selectLog(path: Path): Unit = logViewTabPane.selectLog(path)

  /** Adds a new logfile to display */
  def addLogFile(lrd: LogFileSettings): Unit = {
    logViewTabPane.addLogFile(lrd)
  }

  /**
   * replaces existing log file tab with given one, if it does not exist yet create one.
   */
  def updateLogFileSettings(logFileSettings: LogFileSettings): Unit = {
    ???
  }

  def contains(path: Path): Boolean = logViewTabPane.contains(path)


}



