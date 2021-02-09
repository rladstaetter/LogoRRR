package net.ladstatt.logboard

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane
import net.ladstatt.logboard.views.LogView
import net.ladstatt.util.CanLog

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}


class LogBoardMainBorderPane extends BorderPane with CanLog {

  val squareWidthProperty = new SimpleIntegerProperty(7)
  val canvasWidthProperty = new SimpleIntegerProperty(1000)


  def getSquareWidth(): Int = squareWidthProperty.get

  def setCanvasWidth(width: Int): Unit = canvasWidthProperty.set(width)

  def getCanvasWidth(): Int = canvasWidthProperty.get()

  val tabPane = new LogViewTabPane
  tabPane.canvasWidthProperty.bind(canvasWidthProperty)
  tabPane.squareWidthProperty.bind(squareWidthProperty)
  /*
  canvasWidthProperty.addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
      for (t <- tabPane.getTabs.asScala) {
        t match {
          case logView: LogView => {
            if (logView.isSelected) {
              logView.doRepaint(t1.intValue())
            } else {
              logView.setRepaint(true)
            }
          }
          case _ => // do nothing
        }
      }
    }
  })
*/
  setOnDragOver(new EventHandler[DragEvent] {
    override def handle(event: DragEvent): Unit = {
      if (event.getDragboard.hasFiles) {
        event.acceptTransferModes(TransferMode.ANY: _*)
      }
    }
  })

  setOnDragDropped(new EventHandler[DragEvent] {
    override def handle(event: DragEvent): Unit = {
      val logFile: Path = event.getDragboard.getFiles.get(0).toPath
      if (Files.isReadable(logFile) && Files.isRegularFile(logFile)) {
        Try(LogReport(logFile)) match {
          case Success(value) =>
            timeR({
              tabPane.getTabs.add(new LogView(value, getSquareWidth(), getCanvasWidth()))
              tabPane.getSelectionModel.selectLast()
            }, "Displays logfile")

          case Failure(exception) =>
            System.err.println("Could not import file " + logFile.toAbsolutePath, " reason: " + exception.getMessage)
        }
      }
    }
  })

  setCenter(tabPane)


}



