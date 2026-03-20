package app.logorrr.views.logfiletab

import app.logorrr.clv.color.ColorPicker
import app.logorrr.clv.{ChunkListView, ElementSelector, Vizor}
import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.*
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ObjectPropertyBase, ReadOnlyDoubleProperty, SetPropertyBase, SimpleIntegerProperty}
import javafx.collections.ObservableList


object LogoRRRChunkListView extends UiNodeFileIdAware:

  def apply(mutLogFileSettings: MutLogFileSettings
            , entries: ObservableList[LogEntry]
            , selectInTextView: LogEntry => Unit
            , widthProperty: ReadOnlyDoubleProperty
           ): LogoRRRChunkListView =
    val logEntryVizor = LogEntryVizor(
      mutLogFileSettings.sharedElementSelection
      , widthProperty
      , mutLogFileSettings.blockSizeProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty)
    val logEntrySelector = LogEntrySelector(mutLogFileSettings.sharedElementSelection)
    val picker = new LogEntryPicker(mutLogFileSettings)
    new LogoRRRChunkListView(entries
      , mutLogFileSettings.sharedElementSelection
      , mutLogFileSettings.blockSizeProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty
      , selectInTextView
      , logEntryVizor
      , picker
      , logEntrySelector)

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogoRRRChunkListView])


class LogoRRRChunkListView(override val elements: ObservableList[LogEntry]
                           , override val sharedElementSelection: SetPropertyBase[Int]
                           , override val blockSizeProperty: SimpleIntegerProperty
                           , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                           , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
                           , selectInTextView: LogEntry => Unit
                           , logEntryVizor: Vizor[LogEntry]
                           , logEntryPicker: ColorPicker[LogEntry]
                           , logEntrySelector: ElementSelector[LogEntry])
  extends ChunkListView[LogEntry](
    elements
    , sharedElementSelection
    , blockSizeProperty
    , firstVisibleTextCellIndexProperty
    , lastVisibleTextCellIndexProperty
    , selectInTextView
    , logEntryVizor
    , logEntryPicker
    , logEntrySelector) with BoundId(LogoRRRChunkListView.uiNode(_).value):


  def init(fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    bindIdProperty(fileIdProperty)
    init()


  override def shutdown(): Unit =
    super.shutdown()
    unbindIdProperty()
