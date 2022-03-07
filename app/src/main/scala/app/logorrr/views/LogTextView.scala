package app.logorrr.views

import app.logorrr.model.LogEntry
import app.logorrr.util.{ClipBoardUtils, LogoRRRFonts}
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.input.{Clipboard, ClipboardContent}
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.time.Instant
import scala.collection.immutable.ListMap
import scala.collection.mutable
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
                        , timings: Map[Long, Instant]
                        , maxDuration: FiniteDuration) extends BorderPane {

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
              println(s"timings.size ${timings.size}, linenumber: ${e.lineNumber + 1}")
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


class LogTextView(filteredList: FilteredList[LogEntry]
                  , timings: Map[Long, Instant]
                  , maxDuration: FiniteDuration = 1000 millis) extends BorderPane {

  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength = filteredList.size().toString.length

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(filteredList)
    lv
  }

  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())
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
              setGraphic(new LogTextView.LogEntryElement(e, maxLength, timings, maxDuration))
            case None =>
              setText(e.value)
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


  def selectEntryByIndex(index: Int): Unit = {
    listView.getSelectionModel.select(index)
    listView.scrollTo(index)
  }

}


