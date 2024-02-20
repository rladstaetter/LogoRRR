package app.logorrr.views.text

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.text.contextactions.{IgnoreAboveMenuItem, IgnoreBelowMenuItem}
import javafx.collections.transformation.FilteredList
import javafx.scene.control._

import scala.jdk.CollectionConverters._

class LogTextView(mutLogFileSettings: MutLogFileSettings
                  , filteredList: FilteredList[LogEntry])
  extends ListView[LogEntry]
    with CanLog {

  def scrollToItem(item: LogEntry): Unit = {
    getSelectionModel.select(item)
    val relativeIndex = getItems.indexOf(item)
    getSelectionModel.select(relativeIndex)
    val cellHeight = mutLogFileSettings.getFontSize
    JfxUtils.scrollTo[LogEntry](this, cellHeight, relativeIndex)
  }

  def scrollToActiveLogEntry(): Unit = {
    if (getHeight != 0) {
      logTrace(s"scrollToActiveLogEntry: LogTextView.getHeight: $getHeight")
      val candidates = filteredList.filtered(l => l.lineNumber == mutLogFileSettings.selectedLineNumberProperty.get())
      if (!candidates.isEmpty) {
        Option(candidates.get(0)) match {
          case Some(selectedEntry) =>
            scrollToItem(selectedEntry)
            // to trigger ChunkListView scrollTo and repaint
            mutLogFileSettings.setSelectedLineNumber(selectedEntry.lineNumber)
          case None => // do nothing
        }
      } else {
        logTrace(s"NOT scrollToActiveLogEntry: no active element was set, not changing scroll position.")
      }
    } else {
      logTrace(s"NOT scrollToActiveLogEntry: LogTextView.getHeight: $getHeight")
    }
  }

  def init(): Unit = {
    getStylesheets.add(getClass.getResource("/app/logorrr/LogTextView.css").toExternalForm)
    setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())
    setItems(filteredList)

    getSelectionModel.selectedItemProperty().addListener(JfxUtils.onNew[LogEntry](e => {
      Option(e) match {
        case Some(value) =>
          // implicitly sets active element in chunkview via global settings binding
          mutLogFileSettings.setSelectedLineNumber(value.lineNumber)
        case None => logTrace("Selected item was null")
      }
    }))

    // when changing font size, repaint
    mutLogFileSettings.fontSizeProperty.addListener(JfxUtils.onNew[Number](_ => {
      refresh() // otherwise listview is not repainted correctly since calculation of the cellheight is broken atm
    }))
  }

  /** determine width of max elems in this view */
  def maxLength: Int = filteredList.size().toString.length

  class LogEntryListCell extends ListCell[LogEntry] {
    styleProperty().bind(mutLogFileSettings.fontStyleBinding)
    setGraphic(null)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)

      Option(t) match {
        case Some(e) => calculateLabel(e)
        case None =>
          setGraphic(null)
          setContextMenu(null)
      }
    }

    private def calculateLabel(e: LogEntry): Unit = {
      val entry = LogTextViewLabel(e
        , maxLength
        , mutLogFileSettings.filtersProperty.get().asScala.toSeq
        , mutLogFileSettings.fontStyleBinding
        , mutLogFileSettings.fontSizeProperty)

      setGraphic(entry)

      val ignoreAboveMenuItem = new IgnoreAboveMenuItem(mutLogFileSettings
        , e
        , filteredList
        , scrollToActiveLogEntry)
      val ignoreBelowMenuItem = new IgnoreBelowMenuItem(e, filteredList)

      setContextMenu(new ContextMenu(ignoreAboveMenuItem, ignoreBelowMenuItem))
    }
  }
}





