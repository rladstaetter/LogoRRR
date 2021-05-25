package app.logorrr.views

import app.logorrr.LogEntry
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{ContextMenu, ListCell, ListView, MenuItem}
import javafx.scene.layout.BorderPane
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent

object ClipBoardUtils {

  def copyToClipboardText(s: String): Unit = {
    val clipboard = Clipboard.getSystemClipboard()
    val content = new ClipboardContent()
    content.putString(s)
    clipboard.setContent(content)
  }
}

class LogTextView(filteredList: FilteredList[LogEntry]) extends BorderPane {

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(filteredList)
    lv
  }
  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())

  setCenter(listView)

  class LogEntryListCell extends ListCell[LogEntry] {

    setStyle("""-fx-font: 12pt "Courier"""")
    setGraphic(null)

    val cm = new ContextMenu()
    val copyCurrentToClipboard = new MenuItem("copy to clipboard")
    cm.getItems.add(copyCurrentToClipboard)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          setText(e.value)
          copyCurrentToClipboard.setOnAction(_ => {
            ClipBoardUtils.copyToClipboardText(e.value)
          })
          setContextMenu(cm)
        case None =>
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
