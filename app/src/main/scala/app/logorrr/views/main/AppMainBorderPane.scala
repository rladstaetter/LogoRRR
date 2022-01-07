package app.logorrr.views.main

import app.logorrr.conf.Settings
import app.logorrr.model.LogReport
import app.logorrr.util.CanLog
import app.logorrr.views.{LogFormatLearnerStage, LogView, LogViewTabPane}
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Button
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.{Failure, Success, Try}

object AppMainBorderPane {

  def apply(settings: Settings, reInitMenuBarFn: => Unit): AppMainBorderPane = {
    new AppMainBorderPane(settings.stageSettings.width, settings.squareImageSettings.width, reInitMenuBarFn)
  }
}

/**
 * Main UI element, all other gui elements are in some way children of this Borderpane
 *
 * @param initialSceneWidth  initial width of scene
 * @param initialSquareWidth width of squares to paint in visual view
 */
class AppMainBorderPane(initialSceneWidth: Int
                        , initialSquareWidth: Int
                        , reInitMenuBarFn: => Unit) extends BorderPane with CanLog {

  val sceneWidthProperty = new SimpleIntegerProperty(initialSceneWidth)

  val squareWidthProperty = new SimpleIntegerProperty(initialSquareWidth)

  val logViewTabPane = LogViewTabPane(this)

  private val learnLogFormatButton = new Button("Learn log format")

  learnLogFormatButton.setOnAction(_ => {
    if (logViewTabPane.getTabs.asScala.nonEmpty) {
      logViewTabPane.getTabs.asScala.head match {
        case lv: LogView =>
          val entries = lv.logReport.entries.asScala
          if (entries.nonEmpty) {
            val stage = LogFormatLearnerStage(entries.head)
            stage.showAndWait()
            stage.getLogColumnDef()
          }
        case x =>
          println(x.getClass)
      }
    }
  })

  setCenter(logViewTabPane)

  /** needed to activate drag'n drop */
  setOnDragOver((event: DragEvent) => {
    if (event.getDragboard.hasFiles) {
      event.acceptTransferModes(TransferMode.ANY: _*)
    }
  })

  /** try to interpret dropped element as log file */
  setOnDragDropped((event: DragEvent) => {
    for (f <- event.getDragboard.getFiles.asScala) {
      val path = f.toPath
      if (Files.exists(path)) {
        Settings.updateRecentFileSettings(rf => rf.copy(files = path.toAbsolutePath.toString +: rf.files))
        reInitMenuBarFn
        addLogFile(path)
      }
    }
    selectLastLogFile()
  })

  def shutdown(): Unit = {
    logViewTabPane.shutdown()
    logViewTabPane.getTabs.clear()
  }

  /** called when width of scene changes */
  def setSceneWidth(width: Int): Unit = sceneWidthProperty.set(width)

  /** select log file which is last in tabPane */
  def selectLastLogFile(): Unit = {
    logViewTabPane.getSelectionModel.selectLast() // select last added file repaint it on selection
  }

  /** Adds a new logfile to display */
  def addLogFile(logFile: Path): Unit = {
    if (Files.isReadable(logFile) && Files.isRegularFile(logFile)) {
      Try(LogReport(logFile)) match {
        case Success(value) =>
          logInfo(s"Opening ${logFile.toAbsolutePath.toString} ... ")
          addLogReport(value)
        case Failure(exception) => logError("Could not import file " + logFile.toAbsolutePath + ", reason: " + exception.getMessage)
      }
    } else {
      logWarn(s"Could not read ${logFile.toAbsolutePath} ...")
    }
  }

  private def addLogReport(value: LogReport): Unit = {
    logViewTabPane.add(value)
  }
}



