package app.logorrr.views.settings.timer

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.{ListCell, ListView}
import javafx.scene.layout.BorderPane

object TimerSettingsLogView {

  val entriesToShow = 13

  def mkEntriesToShow(logEntries: ObservableList[LogEntry]): ObservableList[LogEntry] = {
    val subList = logEntries.subList(0, if (logEntries.size() >= TimerSettingsLogView.entriesToShow) TimerSettingsLogView.entriesToShow else logEntries.size())
    FXCollections.observableArrayList(subList)
  }

}

class TimerSettingsLogView(settings: MutLogFileSettings
                           , logEntries: ObservableList[LogEntry]) extends BorderPane {

  val startColProperty: ObjectProperty[java.lang.Integer] = new SimpleObjectProperty[java.lang.Integer](null)
  val endColProperty: ObjectProperty[java.lang.Integer] = new SimpleObjectProperty[java.lang.Integer](null)

  settings.someLogEntrySettingsProperty.get() match {
    case Some(s) =>
      setStartCol(s.startCol)
      setEndCol(s.endCol)
    case None =>
  }

  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength: Int = logEntries.size().toString.length

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.getStyleClass.add("dense")
    lv.setItems(logEntries)
    lv
  }

  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())

  setCenter(listView)

  def setStartCol(i: Int): Unit = startColProperty.set(i)

  def setEndCol(i: Int): Unit = endColProperty.set(i)

  class LogEntryListCell extends ListCell[LogEntry] {
    styleProperty().bind(settings.fontStyleBinding)
    setGraphic(null)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          setText(null)
          setGraphic(TimerSettingsLogViewLabel(settings
            , e
            , maxLength
            , startColProperty
            , endColProperty))
        case None =>
          setGraphic(null)
          setText(null)
      }
    }

  }
}


