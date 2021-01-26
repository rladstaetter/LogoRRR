package net.ladstatt.logboard

import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, ScrollPane}
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

class LogboardController extends Initializable with CanLog {


  val logEntries = new SimpleListProperty[LogEntry](FXCollections.observableArrayList())

  @FXML var borderPane: BorderPane = _
  @FXML var scrollPane: ScrollPane = _
  //@FXML var flowPane: FlowPane = _
  @FXML var selectFileButton: Button = _
  @FXML var logElementCanvas: ImageView = _

  /*
  def clearLogEntries(): Unit = timeR({
    logTrace("Clearing " + flowPane.getChildren.size() + " elements ... start")
    Option(logEntries.get()) match {
      case Some(entries) =>
        entries.parallelStream().forEach(e => {
          e.someTooltip match {
            case Some(t) =>
              //logTrace("Uninstalling element ...")
              Tooltip.uninstall(flowPane, t)
            case None =>
            // logTrace("Clearing element ...")
          }
        })
      case None =>
    }
    logTrace("Clearing " + flowPane.getChildren.size() + " elements ... stop")
  }, "Clearing log entries ...")
*/
  /*
  def setLogEntries(entries: java.util.List[LogEntry]): Unit = {
    logTrace("starting to add all log entries")
    timeR(logEntries.addAll(entries), "add all log entries")

    logTrace("starting to create rectangles")
    val rectangles = timeR(entries.stream.map(_.rectangle).collect(Collectors.toList[Rectangle]()), "create rects")


    flowPane.setVisible(false)
    timeR(flowPane.getChildren.clear(), "clearing rectangles")
    timeR(flowPane.getChildren.setAll(rectangles), "setting rects")
    flowPane.setVisible(true)
    //scrollPane.setContent(null)
    /*
    entries.forEach(e => {
      flowPane.getChildren.add(e.rectangle)
    })
*/
  }
   */

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
      setLogReport(LogReport(logFile))
    }
  }

  val squareWidthProperty = new SimpleIntegerProperty(5)
  val widthProperty = new SimpleIntegerProperty(1200)

  def getSquareWidth(): Int = squareWidthProperty.get

  def setWidth(width: Int): Unit = {
    widthProperty.set(width)
  }

  def getWidth(): Int = widthProperty.get()

  val logReportProperty = new SimpleObjectProperty[LogReport]()

  def getLogReport(): LogReport = logReportProperty.get()

  def setLogReport(l: LogReport): Unit = logReportProperty.set(l)

  def paint(logReport: LogReport
            , squareWidth: Int
            , canvasWidth: Int): Unit = {
    scrollPane.setContent(new ImageView(Painter.paint(logReport.entries, squareWidth, canvasWidth)))
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {

    widthProperty.addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        // println(getSquareWidth + " " + t1.intValue())
        paint(getLogReport(), getSquareWidth(), t1.intValue)
      }
    })
    logReportProperty.addListener(new ChangeListener[LogReport] {
      override def changed(observableValue: ObservableValue[_ <: LogReport], t: LogReport, t1: LogReport): Unit = {
        paint(t1, getSquareWidth(), getWidth())
      }
    })
    selectFileButton.setOnAction((t: ActionEvent) => {
      val c = new FileChooser()
      c.setTitle("Select log file")
      Option(c.showOpenDialog(null)) match {
        case Some(file) => setLogReport(LogReport(file.toPath))
        case None =>
      }
    })
    // setLogReport(LogReport(Paths.get("/Users/lad/gh/javafx-logboard/logfiles/logic.4.log")))
    //    setLogFile(Paths.get("/Users/lad/gh/javafx-logboard/logfiles/ui-admin.0.log"))

  }

}



