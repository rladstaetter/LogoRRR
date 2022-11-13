package app.logorrr.views.settings

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.{ListCell, ListView}
import javafx.scene.layout.BorderPane

object TimerSettingsLogView {

  val entriesToShow = 10

  def mkEntriesToShow(logEntries: ObservableList[LogEntry]): ObservableList[LogEntry] = {
    val subList = logEntries.subList(0, if (logEntries.size() >= TimerSettingsLogView.entriesToShow) TimerSettingsLogView.entriesToShow else logEntries.size())
    FXCollections.observableArrayList(subList)
  }

}

class TimerSettingsLogView(pathAsString: String
                           , logEntries: ObservableList[LogEntry]) extends BorderPane {

  private val mutLogFileSettings: MutLogFileSettings = LogoRRRGlobals.getLogFileSettings(pathAsString)

  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength = logEntries.size().toString.length

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.getStyleClass.add("dense")
    lv.setItems(logEntries)
    lv
  }

  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())

  setCenter(listView)

  class LogEntryListCell extends ListCell[LogEntry] {
    styleProperty().bind(mutLogFileSettings.fontStyle)
    setGraphic(null)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          calculateLabel(e)
        case None =>
          setGraphic(null)
          setText(null)
          setContextMenu(null)
      }
    }

    private def calculateLabel(e: LogEntry): Unit = {
      setText(null)
      val entry = TimerSettingsLogViewLabel(mutLogFileSettings, e, maxLength)
      setGraphic(entry)
    }

  }
}


