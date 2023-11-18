package app.logorrr.views.text

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ClipBoardUtils, JfxUtils}
import javafx.beans.value.ChangeListener
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
  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength: Int = filteredList.size().toString.length


  private val selectedLineNumberListener: ChangeListener[Number] = JfxUtils.onNew((n: Number) => {
    Option(getItems.filtered((t: LogEntry) => t.lineNumber == n.intValue()).get(0)) match {
      case Some(value) =>
        val relativeIndex = getItems.indexOf(value)
        getSelectionModel.select(relativeIndex)
        scrollTo(relativeIndex - ((getHeight / LogTextView.fixedCellSize) / 2).toInt)
      case None =>
    }
  })

  init()

  /**
   *
   */
  def init(): Unit = {
    getStyleClass.add("dense")
    setItems(filteredList)
    val i = mutLogFileSettings.selectedLineNumberProperty.get()
    getSelectionModel.select(i)

    getSelectionModel.selectedIndexProperty().addListener(JfxUtils.onNew({ i: Number => {
      mutLogFileSettings.selectedLineNumberProperty.removeListener(selectedLineNumberListener)
      mutLogFileSettings.setSelectedLineNumber(i.intValue())
      mutLogFileSettings.selectedLineNumberProperty.addListener(selectedLineNumberListener)
    }
    }))
    setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())
    mutLogFileSettings.selectedLineNumberProperty.addListener(selectedLineNumberListener)

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
          setText(null)
          setContextMenu(null)
      }
    }

    private def calculateLabel(e: LogEntry): Unit = {
      setText(null)

      val entry = LogTextViewLabel(e
        , maxLength
        , mutLogFileSettings.filtersProperty.get().asScala.toSeq
        , mutLogFileSettings.fontStyleBinding)
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


