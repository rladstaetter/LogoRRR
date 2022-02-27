package app.logorrr.views

import app.logorrr.model.LogEntry
import app.logorrr.util.{ClipBoardUtils, LogoRRRFonts}
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import javafx.scene.input.{Clipboard, ClipboardContent}
import javafx.scene.layout.BorderPane
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.time.Instant
import scala.collection.mutable


object LogTextView {

  class LineDecoratorLabel extends Label {
    setStyle(LogoRRRFonts.jetBrainsMono(12) + "-fx-background-color: BISQUE;")
    setText("")
  }


}


class LogTextView(filteredList: FilteredList[LogEntry]) extends BorderPane {

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

    cm.getItems.addAll(copyCurrentToClipboard)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          val l = new LogTextView.LineDecoratorLabel
          l.setText(e.lineNumber.toString.reverse.padTo(maxLength, " ").reverse.mkString)
          setGraphic(l)
          setText(e.value)
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


