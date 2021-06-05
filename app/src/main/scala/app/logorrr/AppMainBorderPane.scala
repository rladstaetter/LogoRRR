package app.logorrr

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane
import app.util.CanLog

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.{Failure, Success, Try}

/**
 * Main UI element, all other gui elements are in some way children of this Borderpane
 *
 * @param initialSceneWidth  initial width of scene
 * @param initialSquareWidth width of squares to paint in visual view
 */
class AppMainBorderPane(initialSceneWidth: Int
                        , initialSquareWidth: Int) extends BorderPane with CanLog {

  val sceneWidthProperty = new SimpleIntegerProperty(initialSceneWidth)

  val squareWidthProperty = new SimpleIntegerProperty(initialSquareWidth)

  val tabPane = LogViewTabPane(this)

  setCenter(tabPane)

  /** needed to activate drag'n drop */
  setOnDragOver((event: DragEvent) => {
    if (event.getDragboard.hasFiles) {
      event.acceptTransferModes(TransferMode.ANY: _*)
    }
  })

  /** try to interpret dropped element as log file */
  setOnDragDropped((event: DragEvent) => {
    for (f <- event.getDragboard.getFiles.asScala) {
      addLogFile(f.toPath)
    }
    selectLastLogFile()
  })


  def shutdown(): Unit = tabPane.shutdown()


  /** called when width of scene changes */
  def setSceneWidth(width: Int): Unit = sceneWidthProperty.set(width)

  /** select log file which is last in tabPane */
  def selectLastLogFile(): Unit = {
    tabPane.getSelectionModel.selectLast() // select last added file repaint it on selection
  }

  /** Adds a new logfile to display */
  def addLogFile(logFile: Path): Unit = {
    if (Files.isReadable(logFile) && Files.isRegularFile(logFile)) {
      Try(LogReport(logFile)) match {
        case Success(value) =>
          logInfo(s"Opening ${logFile.toAbsolutePath.toString} ... ")
          tabPane.add(value)
        case Failure(exception) => logError("Could not import file " + logFile.toAbsolutePath + ", reason: " + exception.getMessage)
      }
    } else {
      logWarn(s"Could not read ${logFile.toAbsolutePath} ...")
    }
  }
}



