package net.ladstatt.logorrr.views

import javafx.collections.transformation.FilteredList
import javafx.scene.control.{ListCell, ListView}
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import net.ladstatt.logorrr.LogEntry

class LogTextView(filteredList: FilteredList[LogEntry]) extends BorderPane {

  private val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(filteredList)
    lv
  }

  class LogEntryListCell extends ListCell[LogEntry] {

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t).foreach {
        e =>
          setText(e.value)
          setStyle("""-fx-font: 10pt "Courier"""")
        /*
        if (e.filter == DefaultFilter.severe) {
          setStyle("""-fx-font: 10pt "Courier"; -fx-background-color: red;""")
        }

         */
      }
    }
  }

  listView.setCellFactory(new Callback[ListView[LogEntry], ListCell[LogEntry]] {
    override def call(p: ListView[LogEntry]): ListCell[LogEntry] = {
      new LogEntryListCell()
    }
  })

  def selectEntryByIndex(index: Int): Unit = {
    listView.getSelectionModel.select(index)
    listView.scrollTo(index)
  }

  setCenter(listView)
}
