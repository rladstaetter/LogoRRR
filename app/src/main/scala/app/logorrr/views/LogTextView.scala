package app.logorrr.views

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntry
import app.logorrr.util.{ClipBoardUtils, JfxUtils, LogoRRRFonts}
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

import java.time.Instant
import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.language.postfixOps


object LogTextView {

  val size = 12

  val timeBarColor = Color.BISQUE.darker()
  val timeBarOverflowColor = timeBarColor.darker()

  object LineDecoratorLabel {

    def apply(logEntry: LogEntry, maxLength: Int): LineDecoratorLabel = {
      val ldl = new LineDecoratorLabel
      ldl.setText(logEntry.lineNumber.toString.reverse.padTo(maxLength, " ").reverse.mkString)
      ldl
    }
  }

  class LineDecoratorLabel extends Label {
    val c = Color.web("bisque")
    setStyle(LogoRRRFonts.jetBrainsMono(size) + "-fx-background-color: BISQUE;")
    setText("")
  }

  class LogEntryElement(e: LogEntry
                        , maxLength: Int
                        , timings: Map[Int, Instant]
                        , maxDuration: FiniteDuration) extends BorderPane {

    /*
        val hBox = new HBox()

        val bg = new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(size * 1.25), Insets.EMPTY))

        private val labels: Seq[Label] =
          for ((c, i) <- Seq(e.value, e.value, e.value).zipWithIndex) yield {
            val l = new Label(c)
            if (i % 2 == 0) {
              l.setBackground(bg)
              l.setTextFill(Color.BLACK)
            }
            //l.setStyle("-fx-background: rgb(255,0,255);")
            // l.setTextFill(Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255)))
            l
          }
        hBox.getChildren.addAll(labels: _*)
        BorderPane.setAlignment(hBox, Pos.CENTER_LEFT)
        setLeft(LineDecoratorLabel(e, maxLength))
        setCenter(hBox)

        hBox.getChildren.addAll(labels: _*)
        BorderPane.setAlignment(hBox, Pos.CENTER_LEFT)
        setLeft(LineDecoratorLabel(e, maxLength))
        setCenter(hBox)

        hBox.getChildren.addAll(labels: _*)
        BorderPane.setAlignment(hBox, Pos.CENTER_LEFT)
        setLeft(LineDecoratorLabel(e, maxLength))
        setCenter(hBox)
      */

    val label = new Label(e.value)
    BorderPane.setAlignment(label, Pos.CENTER_LEFT)
    setLeft(LineDecoratorLabel(e, maxLength))
    setCenter(label)


    val hb = new HBox()
    e.someInstant match {
      case Some(instant) =>
        val duration: Long =
          timings.get(e.lineNumber + 1) match {
            case Some(nextTi) =>
              nextTi.toEpochMilli - instant.toEpochMilli
            case None =>
              //   println(s"timings.size ${timings.size}, linenumber: ${e.lineNumber + 1}")
              0L
          }
        if (duration > maxDuration.toMillis) {
          val left = new Rectangle(maxDuration.toMillis, 10)
          left.setFill(LogTextView.timeBarColor)
          val right = new Rectangle(2, 10)
          right.setFill(LogTextView.timeBarOverflowColor)
          hb.getChildren.addAll(left, right)
          setBottom(hb)
        } else {
          val left = new Rectangle(duration.toDouble, 10)
          left.setFill(LogTextView.timeBarColor)
          hb.getChildren.addAll(left)
          setBottom(hb)
        }
      case None =>
    }
  }

}


class LogTextView(pathAsString: String
                  , filteredList: FilteredList[LogEntry]
                  , timings: Map[Int, Instant]
                  , maxDuration: FiniteDuration = 1200 millis) extends BorderPane {

  private val fixedCellSize = 26

  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength = filteredList.size().toString.length

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(filteredList)
    val i = LogoRRRGlobals.getLogFileSettings(pathAsString).selectedIndexProperty.get()
    lv.getSelectionModel.select(i)
    lv
  }
  listView.heightProperty().addListener(JfxUtils.onNew((s: Number) => println("jo " + s.doubleValue())))
  listView.heightProperty().addListener(JfxUtils.onNew((n: Number) => println("jasdfo" + n.doubleValue() / 26.0)))
  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())
  listView.setFixedCellSize(fixedCellSize)

  LogoRRRGlobals.getLogFileSettings(pathAsString).selectedIndexProperty.addListener(JfxUtils.onNew((n: Number) => {
    Option(listView.getItems.filtered((t: LogEntry) => t.lineNumber == n.intValue()).get(0)) match {
      case Some(value) =>
        val relativeIndex = listView.getItems.indexOf(value)
        listView.getSelectionModel.select(relativeIndex)
        listView.scrollTo(relativeIndex - ((listView.getHeight / fixedCellSize) / 2).toInt)
        println(s"selectedIndex : ${n.intValue()}, scrollTo : $relativeIndex")
      case None =>
    }

  }))


  setCenter(listView)

  class LogEntryListCell extends ListCell[LogEntry] {

    setStyle(LogoRRRFonts.jetBrainsMono(12))
    setGraphic(null)
    val cm = new ContextMenu()
    val copyCurrentToClipboard = new MenuItem("copy text to clipboard")

    cm.getItems.add(copyCurrentToClipboard)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          e.someInstant match {
            case Some(_) =>
              setText(null)
              setGraphic(new LogTextView.LogEntryElement(e, maxLength, timings, maxDuration))
            case None =>
              setText(e.value)
              setGraphic(null)
          }
          copyCurrentToClipboard.setOnAction(_ => ClipBoardUtils.copyToClipboardText(e.value))
          setContextMenu(cm)
        case None =>
          setGraphic(null)
          setText(null)
          setContextMenu(null)
      }
    }

  }

  def select(logEntry: LogEntry): Unit = {
    listView.getSelectionModel.select(logEntry)
    val index = listView.getSelectionModel.getSelectedIndex - ((listView.getHeight / fixedCellSize) / 2).toInt
    listView.scrollTo(index)
  }

}


