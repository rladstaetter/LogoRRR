package app.logorrr.views.text

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.text.contextactions.{CopyEntriesMenuItem, IgnoreAboveMenuItem, IgnoreBelowMenuItem}
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import net.ladstatt.util.log.CanLog

import scala.jdk.CollectionConverters._


object LogTextView extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogTextView])

}

class LogTextView(mutLogFileSettings: MutLogFileSettings
                  , filteredList: FilteredList[LogEntry])
  extends ListView[LogEntry]
    with CanLog {

  setId(LogTextView.uiNode(mutLogFileSettings.getFileId).value)
  getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

  private lazy val selectedLineNumberListener = JfxUtils.onNew[LogEntry](e => {
    Option(e) match {
      case Some(value) =>
        // implicitly sets active element in chunkview via global settings binding
        mutLogFileSettings.setSelectedLineNumber(value.lineNumber)
      case None => logTrace("Selected item was null")
    }
  })

  // when changing font size, repaint
  // otherwise listview is not repainted correctly since calculation of the cellheight is broken atm
  private lazy val refreshListener = JfxUtils.onNew[Number](_ => refresh())

  private lazy val scrollBarListener = JfxUtils.onNew[Number](_ => {
    val (first, last) = ListViewHelper.getVisibleRange(this)
    mutLogFileSettings.setFirstVisibleTextCellIndex(first)
    mutLogFileSettings.setLastVisibleTextCellIndex(last)
  })

  /**
   * to observe the visible text and mark it in the boxview
   */
  private lazy val skinListener = JfxUtils.onNew[Skin[_]](_ => {
    ListViewHelper.findScrollBar(this).foreach(_.valueProperty.addListener(scrollBarListener))
  })


  def scrollToItem(item: LogEntry): Unit = {
    val relativeIndex = getItems.indexOf(item)
    getSelectionModel.clearAndSelect(relativeIndex)
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

    getSelectionModel.selectedItemProperty().addListener(selectedLineNumberListener)
    mutLogFileSettings.fontSizeProperty.addListener(refreshListener)

    skinProperty.addListener(skinListener)

    setItems(filteredList)

  }


  /** clean up listeners */
  def removeListeners(): Unit = {
    getSelectionModel.selectedItemProperty().removeListener(selectedLineNumberListener)
    mutLogFileSettings.fontSizeProperty.removeListener(refreshListener)
    skinProperty.removeListener(skinListener)
    ListViewHelper.findScrollBar(this).foreach(_.valueProperty.removeListener(scrollBarListener))
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

      val copySelectionMenuItem = new CopyEntriesMenuItem(getSelectionModel)

      val ignoreAboveMenuItem = new IgnoreAboveMenuItem(mutLogFileSettings
        , e
        , filteredList
        , scrollToActiveLogEntry)
      val ignoreBelowMenuItem = new IgnoreBelowMenuItem(e, filteredList)

      setContextMenu(new ContextMenu(copySelectionMenuItem, ignoreAboveMenuItem, ignoreBelowMenuItem))
    }
  }
}





