package net.ladstatt.logboard.views

import javafx.collections.FXCollections
import javafx.scene.control.{Button, ListCell, ListView, ToolBar}
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import net.ladstatt.logboard.{LogEntry, LogReport, LogSeverity}

class LogTextView(logReport: LogReport) extends BorderPane {

  def mkListView(): ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(FXCollections.observableList(logReport.entries))
    lv
  }

  def mkStatsToolbar(): ToolBar = {
    val bar = new ToolBar()
    LogSeverity.seq.stream.forEach(ls => {
      bar.getItems.add(new Button(ls.name + ": " + logReport.occurences.get(ls) + " " + logReport.percentAsString(ls) + "%"))
    })
    bar
  }

  private val listView: ListView[LogEntry] = mkListView()

  class LogEntryListCell extends ListCell[LogEntry] {

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t).foreach {
        e =>
          setText(e.value)
          setStyle("""-fx-font: 10pt "Courier"""")
          if (e.severity == LogSeverity.Severe) {
            setStyle("""-fx-font: 10pt "Courier"; -fx-background-color: red;""")
          }
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

  setTop(mkStatsToolbar())
  setCenter(listView)
}
