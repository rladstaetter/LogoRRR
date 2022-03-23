package app.logorrr.views.main

import app.logorrr.conf.{Settings, SettingsIO}
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.util.CanLog
import app.logorrr.views.{Filter, LogViewTabPane, SimpleRange}
import javafx.application.HostServices
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableStringValue
import javafx.scene.control.Label
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.ListHasAsScala

object AppMainBorderPane {

  def apply(hostServices: HostServices, settings: Settings, reInitMenuBarFn: => Unit): AppMainBorderPane = {
    new AppMainBorderPane(hostServices, settings.stageSettings.width, settings.squareImageSettings.width, reInitMenuBarFn)
  }
}

/**
 * Main UI element, all other gui elements are in some way children of this Borderpane
 *
 * @param initialSceneWidth  initial width of scene
 * @param initialSquareWidth width of squares to paint in visual view
 */
class AppMainBorderPane(hostServices: HostServices
                        , initialSceneWidth: Int
                        , initialSquareWidth: Int
                        , reInitMenuBarFn: => Unit)
  extends BorderPane with CanLog {


  val sceneWidthProperty = new SimpleIntegerProperty(initialSceneWidth)

  val squareWidthProperty = new SimpleIntegerProperty(initialSquareWidth)

  val logViewTabPane = LogViewTabPane(hostServices, this, reInitMenuBarFn)


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
            val logFileDefinition =
              LogFileSettings(path.toAbsolutePath.toString
                , LogFileSettings.DefaultDividerPosition
                , Filter.seq
                , LogFileSettings.DefaultLogFormat)
            SettingsIO.updateRecentFileSettings(rf => rf.copy(logFileDefinitions = Map(logFileDefinition.pathAsString -> logFileDefinition) ++ rf.logFileDefinitions))
            reInitMenuBarFn
            addLogFile(logFileDefinition)
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

  /** called when width of scene changes */
  def setSceneWidth(width: Int): Unit = sceneWidthProperty.set(width)

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
  def updateLogFile(logFileDef: LogFileSettings): Unit = {
    ???
  }

  def contains(path: Path): Boolean = logViewTabPane.contains(path)


}



