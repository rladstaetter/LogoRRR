package net.ladstatt.logboard

import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventHandler
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane
import net.ladstatt.util.CanLog

import java.nio.file.{Files, Path}
import scala.util.{Failure, Success, Try}

/**
 * Main UI element, all other gui elements are in some way children of this Borderpane
 *
 * @param initialSceneWidth  initial width of scene
 * @param initialSquareWidth width of squares to paint in visual view
 */
class LogBoardMainBorderPane(initialSceneWidth: Int
                             , initialSquareWidth: Int) extends BorderPane with CanLog {

  val squareWidthProperty = new SimpleIntegerProperty(initialSquareWidth)

  val sceneWidthProperty = new SimpleIntegerProperty(initialSceneWidth)

  val tabPane = LogViewTabPane(this)

  setCenter(tabPane)


  /** called when width of scene changes */
  def setSceneWidth(width: Int): Unit = sceneWidthProperty.set(width)

  /** needed to activate dragndrop */
  setOnDragOver(new EventHandler[DragEvent] {
    override def handle(event: DragEvent): Unit = {
      if (event.getDragboard.hasFiles) {
        event.acceptTransferModes(TransferMode.ANY: _*)
      }
    }
  })

  /** try to interpret dropped element as log file */
  setOnDragDropped(new EventHandler[DragEvent] {
    override def handle(event: DragEvent): Unit = timeR({
      val logFile: Path = event.getDragboard.getFiles.get(0).toPath
      if (Files.isReadable(logFile) && Files.isRegularFile(logFile)) {
        Try(LogReport(logFile)) match {
          case Success(value) =>
            tabPane.add(value)
          //              tabPane.getSelectionModel.selectLast()

          case Failure(exception) =>
            System.err.println("Could not import file " + logFile.toAbsolutePath, " reason: " + exception.getMessage)
        }
      }
    }, "Added logfile")
  })


}



