package app.logorrr.views.text

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters._

object LogTextView {

  private val fixedCellSize = 26

  val timeBarColor: Color = Color.BISQUE.darker()

  val timeBarOverflowColor: Color = timeBarColor.darker()

}


class LogTextView(mutLogFileSettings: MutLogFileSettings
                  , filteredList: FilteredList[LogEntry]) extends ListView[LogEntry] with CanLog {

  init()

  def init(): Unit = {
    getStylesheets.add(getClass.getResource("/app/logorrr/LogTextView.css").toExternalForm)
    setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())
    setItems(filteredList)
    Option(filteredList.filtered(l => l.lineNumber == mutLogFileSettings.selectedLineNumberProperty.get()).get(0)) match {
      case Some(selectedEntry) =>
        selectLogEntry(selectedEntry)
      case None => // do nothing
    }

    getSelectionModel.selectedItemProperty().addListener(JfxUtils.onNew[LogEntry](e => {
      Option(e) match {
        case Some(value) =>
          // implicitly sets active element in chunkview via global settings binding
          mutLogFileSettings.setSelectedLineNumber(value.lineNumber)
        case None => logTrace("Selected item was null")
      }
    }))

    // when changing font size, repaint
    mutLogFileSettings.fontSizeProperty.addListener(JfxUtils.onNew[Number](n => {
      refresh() // otherwise listview is not repainted correctly since calculation of the cellheight is broken atm
    }))
  }

  /** 'pragmatic way' to determine width of max elems in this view */
  def maxLength: Int = filteredList.size().toString.length

  def selectLogEntry(logEntry: LogEntry): Unit = {
    getSelectionModel.select(logEntry)
    val relativeIndex = getItems.indexOf(logEntry)
    getSelectionModel.select(relativeIndex)
    // font size dictates the height of a cell
    // kind of a hack, has to be enough for the moment
    val cellHeight = mutLogFileSettings.getFontSize
    // to lookup the cell height, equally a hack
    // val cellHeight = lookup(".list-cell").getBoundsInLocal.getHeight
    val visibleItemCount = (getHeight / cellHeight).asInstanceOf[Int]
    val idx = if (relativeIndex - visibleItemCount / 2 <= 0) 0 else relativeIndex - visibleItemCount / 2
    scrollTo(idx)
  }


  class LogEntryListCell extends ListCell[LogEntry] {
    styleProperty().bind(mutLogFileSettings.fontStyleBinding)
    setGraphic(null)

    val ignoreAbove = new MenuItem("Ignore entries above")
    val ignoreBelow = new MenuItem("Ignore entries below")
    val cm = new ContextMenu(ignoreAbove, ignoreBelow)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          calculateLabel(e)
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
      ignoreAbove.setOnAction(_ => {
        val currPredicate = filteredList.getPredicate
        filteredList.setPredicate((entry: LogEntry) => currPredicate.test(entry) && e.lineNumber <= entry.lineNumber)
        logTrace("Ignoring all before " + e.lineNumber)
      })
      ignoreBelow.setOnAction(_ => {
        val currPredicate = filteredList.getPredicate
        filteredList.setPredicate((entry: LogEntry) => currPredicate.test(entry) && entry.lineNumber <= e.lineNumber)
        logTrace("Ignoring all after " + e.lineNumber)
      })

      setContextMenu(cm)
    }
  }
}


