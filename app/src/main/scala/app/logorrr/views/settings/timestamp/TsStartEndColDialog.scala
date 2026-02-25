package app.logorrr.views.settings.timestamp

import app.logorrr.conf.mut.{MutLogFileSettings, MutTimeSettings}
import app.logorrr.conf.{FileId, TimeSettings}
import app.logorrr.model.{BoundId, LogEntry}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ObjectProperty, ObjectPropertyBase, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.scene.control.{ListCell, ListView}
import javafx.scene.layout.BorderPane

object TsStartEndColDialog extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TsStartEndColDialog])


/**
 * Select start and end column via mouseclick
 */
class TsStartEndColDialog(mutLogFileSettings: MutLogFileSettings
                          , logEntries: ObservableList[LogEntry])
  extends BorderPane with BoundId(TsStartEndColDialog.uiNode(_).value):

  val startColProperty: ObjectProperty[java.lang.Integer] = new SimpleObjectProperty[java.lang.Integer](null)
  val endColProperty: ObjectProperty[java.lang.Integer] = new SimpleObjectProperty[java.lang.Integer](null)

  private def setInitialValues(someLocalTs: MutTimeSettings, someGlobalTs: MutTimeSettings): Unit = {
    if (someLocalTs.validBinding.get()) {
      setStartCol(someLocalTs.getStartCol)
      setEndCol(someLocalTs.getEndCol)
    } else if (someGlobalTs.validBinding.get()) {
      setStartCol(someGlobalTs.getStartCol)
      setEndCol(someGlobalTs.getEndCol)
    }
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

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , someLocalTimeSettings: MutTimeSettings
           , someGlobalTimeSettings: MutTimeSettings
           , startColProperty: SimpleObjectProperty[java.lang.Integer]
           , endColProperty: SimpleObjectProperty[java.lang.Integer]
          ): Unit =
    bindIdProperty(fileIdProperty)
    startColProperty.bind(this.startColProperty)
    endColProperty.bind(this.endColProperty)
    setInitialValues(someLocalTimeSettings, someGlobalTimeSettings)

  def shutdown(startColProperty: SimpleObjectProperty[java.lang.Integer]
               , endColProperty: SimpleObjectProperty[java.lang.Integer]): Unit = {
    unbindIdProperty()
    startColProperty.unbind()
    endColProperty.unbind()
  }

  class LogEntryListCell extends ListCell[LogEntry]:
    styleProperty().bind(mutLogFileSettings.fontStyleBinding)
    setGraphic(null)

    override def updateItem(t: LogEntry, b: Boolean): Unit =
      super.updateItem(t, b)
      Option(t) match
        case Some(e) =>
          setText(null)
          setGraphic(TimerSettingsLogViewLabel(listView
            , mutLogFileSettings
            , e
            , maxLength
            , startColProperty
            , endColProperty))
        case None =>
          setGraphic(null)
          setText(null)



