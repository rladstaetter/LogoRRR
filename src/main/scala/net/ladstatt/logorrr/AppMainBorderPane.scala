package net.ladstatt.logorrr

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane
import net.ladstatt.util.CanLog

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

  val squareWidthProperty = new SimpleIntegerProperty(initialSquareWidth)

  val sceneWidthProperty = new SimpleIntegerProperty(initialSceneWidth)

  val tabPane = LogViewTabPane(this)

  setCenter(tabPane)


  /** called when width of scene changes */
  def setSceneWidth(width: Int): Unit = sceneWidthProperty.set(width)

  /** needed to activate dragndrop */
  setOnDragOver((event: DragEvent) => {
    if (event.getDragboard.hasFiles) {
      event.acceptTransferModes(TransferMode.ANY: _*)
    }
  })

  /** try to interpret dropped element as log file */
  setOnDragDropped((event: DragEvent) => timeR({
    for (f <- event.getDragboard.getFiles.asScala) {
      val logFile: Path = f.toPath
      if (Files.isReadable(logFile) && Files.isRegularFile(logFile)) {
        addLogFile(logFile)
      } else {
        logTrace(s"Could not read ${logFile.toAbsolutePath}")
      }
    }
    selectLastLogFile()
  }, "Added logfile"))

  /** select log file which is last in tabPane */
  def selectLastLogFile(): Unit = {
    tabPane.getSelectionModel.selectLast() // select last added file repaint it on selection
  }

  /** Adds a new logfile to display */
  def addLogFile(logFile: Path): Unit = {
    Try(LogReport(logFile)) match {
      case Success(value) => tabPane.add(value)
      case Failure(exception) => logError("Could not import file " + logFile.toAbsolutePath + ", reason: " + exception.getMessage)
    }
  }
}



