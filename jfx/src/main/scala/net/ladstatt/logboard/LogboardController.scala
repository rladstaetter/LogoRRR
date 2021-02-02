package net.ladstatt.logboard

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Tab, TabPane}
import javafx.scene.image.ImageView
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
  @FXML var logElementCanvas: ImageView = _

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
        case Success(value) => addLogReport(value)
        case Failure(exception) =>
      }
    }
  }

  def mkObservableList[T](iterable: Iterable[T] = Nil): ObservableList[T] = {
    val a = new util.ArrayList[T](iterable.toList.asJava)
    FXCollections.observableList(a)
  }

  val squareWidthProperty = new SimpleIntegerProperty(5)
  val widthProperty = new SimpleIntegerProperty(1200)

  def getSquareWidth(): Int = squareWidthProperty.get

  def setWidth(width: Int): Unit = widthProperty.set(width)

  def getWidth(): Int = widthProperty.get()

  def addLogReport(l: LogReport): Unit = {
    val tab = ReportTab(l)
    tabPane.getTabs.add(tab)
    tabPane.getSelectionModel.selectLast()
  }


  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    widthProperty.addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        for (t <- tabPane.getTabs.asScala) {
          t match {
            case tab: ReportTab => tab.paint(getSquareWidth(), t1.intValue())
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
                case reportTab: ReportTab => reportTab.paint(getSquareWidth(), getWidth())
                case _ => // do nothing
              }
            }
          }
        }
      }
    })

    // setLogReport(LogReport(Paths.get("/Users/lad/gh/javafx-logboard/logfiles/logic.4.log")))
    //    setLogFile(Paths.get("/Users/lad/gh/javafx-logboard/logfiles/ui-admin.0.log"))

  }

}



