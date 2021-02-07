package net.ladstatt.logboard

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Tab, TabPane}
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane
import net.ladstatt.util.CanLog

import java.net.URL
import java.nio.file.{Files, Path}
import java.util
import java.util.ResourceBundle
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class LogboardController extends Initializable with CanLog {

  @FXML var borderPane: BorderPane = _
  @FXML var tabPane: TabPane = _

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
      Try(LogReport(logFile)) match {
        case Success(value) =>
          timeR(addLogReport(value), "Displays logfile")
        case Failure(exception) =>
          System.err.println("Could not import file " + logFile.toAbsolutePath, " reason: " + exception.getMessage)
      }
    }
  }

  def mkObservableList[T](iterable: Iterable[T] = Nil): ObservableList[T] = {
    val a = new util.ArrayList[T](iterable.toList.asJava)
    FXCollections.observableList(a)
  }

  val squareWidthProperty = new SimpleIntegerProperty(7)
  val canvasWidthProperty = new SimpleIntegerProperty(1000)

  def getSquareWidth(): Int = squareWidthProperty.get

  def setCanvasWidth(width: Int): Unit = canvasWidthProperty.set(width)

  def getCanvasWidth(): Int = canvasWidthProperty.get()

  def addLogReport(l: LogReport): Unit = {
    val tab = LogView(l, getSquareWidth(), getCanvasWidth())
    tabPane.getTabs.add(tab)
    tabPane.getSelectionModel.selectLast()
  }


  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {

    tabPane.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[Tab] {
      override def changed(observableValue: ObservableValue[_ <: Tab], t: Tab, t1: Tab): Unit = {
        t1 match {
          case tab: LogView =>
            if (tab.getRepaint()) {
              tab.doRepaint(getSquareWidth(), getCanvasWidth())
            }
          case _ =>
        }
      }
    }

    )
    canvasWidthProperty.addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        for (t <- tabPane.getTabs.asScala) {
          t match {
            case tab: LogView => {
              if (tab.isSelected) {
                tab.doRepaint(getSquareWidth(), t1.intValue())
              } else {
                tab.setRepaint(true)
              }
            }
            case _ => // do nothing
          }
        }
      }
    })
    tabPane.getTabs.addListener(new ListChangeListener[Tab] {
      override def onChanged(change: ListChangeListener.Change[_ <: Tab]): Unit = {
        while (change.next) {
          if (change.wasAdded()) {
            for (r <- change.getAddedSubList.asScala) {
              r match {
                case reportTab: LogView => reportTab.doRepaint(getSquareWidth(), getCanvasWidth())
                case _ => // do nothing
              }
            }
          }
        }
      }
    })

  }

}



