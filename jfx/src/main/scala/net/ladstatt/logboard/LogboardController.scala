package net.ladstatt.logboard

import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.event.ActionEvent
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, ScrollPane, Tab, TabPane}
import javafx.scene.image.{Image, ImageView, PixelWriter, WritableImage}
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import net.ladstatt.util.CanLog

import java.net.URL
import java.nio.file.{Files, Path}
import java.util
import java.util.ResourceBundle
import scala.jdk.CollectionConverters._


object Painter {


  def paintSquare(pw: PixelWriter, u: Int, v: Int, length: Int, c: Color): PixelWriter = {
    for {x <- u until (u + length - 1)
         y <- v until (v + length - 1)} {
      //println(s"x:$x, y:$y")
      pw.setColor(x, y, c)
    }
    pw
  }

  def paint(entries: util.List[LogEntry], squareWidth: Int, canvasWidth: Int): Image = {
    val numberCols = canvasWidth / squareWidth
    //val squareLength = width.toDouble / numberCols
    val numRows = entries.size() / numberCols
    val height = squareWidth * numRows
    val wi = new WritableImage((canvasWidth + squareWidth).toInt, (height + squareWidth).toInt)
    //    println(wi.getWidth + " jo " + wi.getHeight + " width " + canvasWidth + " height " + height + " squarelength" + squareWidth)
    val pw = wi.getPixelWriter

    for ((e, i) <- entries.asScala.zipWithIndex) {
      paintSquare(pw, ((i % numberCols) * squareWidth).toInt, ((i / numberCols) * squareWidth).toInt, squareWidth.toInt, e.severity.color)
    }
    wi
  }

}
/*
object ReportTab {

  def apply(r :LogReport) : ReportTab = {
    val rt = new ReportTab
    rt.setUserData(rt)
    rt
  }

}
class ReportTab extends Tab
*/
class LogboardController extends Initializable with CanLog {


  @FXML var borderPane: BorderPane = _
  @FXML var tabPane: TabPane = _
  @FXML var selectFileButton: Button = _
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
      addLogReport(LogReport(logFile))
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

  val reportsProperty = new SimpleListProperty[LogReport](mkObservableList())

  def getLogReport(i: Int): LogReport = reportsProperty.get(i)

  def addLogReport(l: LogReport): Unit = reportsProperty.add(l)

  def paint(tab: Tab
            , logReport: LogReport
            , squareWidth: Int
            , canvasWidth: Int): Unit = {
    tab.setContent(new ScrollPane(new ImageView(Painter.paint(logReport.entries, squareWidth, canvasWidth))))
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    widthProperty.addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        for ((t, r) <- tabPane.getTabs.asScala zip reportsProperty.asScala) {
          paint(t, r, getSquareWidth(), t1.intValue)
        }
      }
    })
    reportsProperty.addListener(new ListChangeListener[LogReport] {
      override def onChanged(change: ListChangeListener.Change[_ <: LogReport]): Unit = {

        while (change.next) {
          if (change.wasAdded()) {
            for (r <- change.getAddedSubList.asScala) {
              val name = r.name + " (" + r.entries.size() + " entries)"
              val tab = new Tab(name)
              tabPane.getTabs.add(tab)
              paint(tab, r, getSquareWidth(), getWidth())
            }
          }
        }
      }
    })
    /*
        reportsProperty.addListener(new ChangeListener[LogReport] {
          override def changed(observableValue: ObservableValue[_ <: LogReport], t: LogReport, t1: LogReport): Unit = {
            for ((t, r) <- tabPane.getTabs.asScala zip reportsProperty.asScala) {
              paint(t, t1, getSquareWidth(), getWidth())
            }
          }
        })

     */
    selectFileButton.setOnAction((t: ActionEvent) => {
      val c = new FileChooser()
      c.setTitle("Select log file")
      Option(c.showOpenDialog(null)) match {
        case Some(file) => addLogReport(LogReport(file.toPath))
        case None =>
      }
    })
    // setLogReport(LogReport(Paths.get("/Users/lad/gh/javafx-logboard/logfiles/logic.4.log")))
    //    setLogFile(Paths.get("/Users/lad/gh/javafx-logboard/logfiles/ui-admin.0.log"))

  }

}



