package app.logorrr.views

import javafx.collections.transformation.FilteredList
import javafx.scene.control.{ListCell, ListView}
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import app.logorrr.LogEntry
import javafx.beans.value.{ChangeListener, ObservableValue}

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

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) => setText(e.value)
        case None => setText(null)
      }
    }
  }

  def selectEntryByIndex(index: Int): Unit = {
    listView.getSelectionModel.select(index)
    listView.scrollTo(index)
  }

}
