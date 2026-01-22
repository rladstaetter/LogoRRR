package app.logorrr.views.settings.timestamp

import app.logorrr.conf.{FileId, LogoRRRGlobals}
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.scene.control.{ListCell, ListView}
import javafx.scene.layout.BorderPane

object TimestampPositionSelectionBorderPane extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimestampPositionSelectionBorderPane])


class TimestampPositionSelectionBorderPane(mutLogFileSettings: MutLogFileSettings
                                           , logEntries: ObservableList[LogEntry]) extends BorderPane:

  setId(TimestampPositionSelectionBorderPane.uiNode(mutLogFileSettings.getFileId).value)

  val startColProperty: ObjectProperty[java.lang.Integer] = new SimpleObjectProperty[java.lang.Integer](null)
  val endColProperty: ObjectProperty[java.lang.Integer] = new SimpleObjectProperty[java.lang.Integer](null)

  mutLogFileSettings.getSomeTimestampSettings match
    case Some(s) =>
      setStartCol(s.startCol)
      setEndCol(s.endCol)
    case None =>
      // check if we have a global defined start/end col
      LogoRRRGlobals.getTimestampSettings match {
        case Some(timestampSettings) =>
          setStartCol(timestampSettings.getStartCol)
          setEndCol(timestampSettings.getEndCol)
        case None => 
      }

  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength: Int = logEntries.size().toString.length

  val listView: ListView[LogEntry] =
    val lv = new ListView[LogEntry]()
    lv.getStyleClass.add("dense")
    lv.setItems(logEntries)
    lv

  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())

  setCenter(listView)

  def setStartCol(i: Int): Unit = startColProperty.set(i)

  def setEndCol(i: Int): Unit = endColProperty.set(i)

  class LogEntryListCell extends ListCell[LogEntry]:
    styleProperty().bind(mutLogFileSettings.fontStyleBinding)
    setGraphic(null)

    override def updateItem(t: LogEntry, b: Boolean): Unit =
      super.updateItem(t, b)
      Option(t) match
        case Some(e) =>
          setText(null)
          setGraphic(TimerSettingsLogViewLabel(mutLogFileSettings
            , e
            , maxLength
            , startColProperty
            , endColProperty))
        case None =>
          setGraphic(null)
          setText(null)



