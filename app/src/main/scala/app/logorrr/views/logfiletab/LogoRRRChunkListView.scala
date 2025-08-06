package app.logorrr.views.logfiletab

import app.logorrr.clv.color.ColorChozzer
import app.logorrr.clv.{ChunkListView, ElementSelector, Vizor}
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.{LogEntry, LogEntryChozzer, LogEntrySelector, LogEntryVizor}
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList


object LogoRRRChunkListView extends UiNodeFileIdAware {

  def apply(entries: ObservableList[LogEntry]
            , mutLogFileSettings: MutLogFileSettings
            , selectInTextView: LogEntry => Unit
            , widthProperty: ReadOnlyDoubleProperty
           ): ChunkListView[LogEntry] = {
    val logEntryVizor = LogEntryVizor(
      mutLogFileSettings.selectedLineNumberProperty
      , widthProperty
      , mutLogFileSettings.blockSizeProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty)
    val logEntryChozzer = LogEntryChozzer(mutLogFileSettings.filtersProperty)
    val logEntrySelector = LogEntrySelector(mutLogFileSettings.selectedLineNumberProperty)

    new LogoRRRChunkListView(entries
      , mutLogFileSettings.selectedLineNumberProperty
      , mutLogFileSettings.blockSizeProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty
      , selectInTextView
      , logEntryVizor
      , logEntryChozzer
      , logEntrySelector
      , mutLogFileSettings.getFileId)
  }

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogoRRRChunkListView])

}

class LogoRRRChunkListView(override val elements: ObservableList[LogEntry]
                           , override val selectedLineNumberProperty: SimpleIntegerProperty
                           , override val blockSizeProperty: SimpleIntegerProperty
                           , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                           , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
                           , selectInTextView: LogEntry => Unit
                           , logEntryVizor: Vizor[LogEntry]
                           , logEntryChozzer: ColorChozzer[LogEntry]
                           , logEntrySelector: ElementSelector[LogEntry]
                           , fileId: FileId)
  extends ChunkListView[LogEntry](elements, selectedLineNumberProperty, blockSizeProperty, firstVisibleTextCellIndexProperty
    , lastVisibleTextCellIndexProperty, selectInTextView,
    logEntryVizor, logEntryChozzer, logEntrySelector) {

  // set Id to track it for UI tests
  setId(LogoRRRChunkListView.uiNode(fileId).value)


}