package app.logorrr.views.text

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntry
import app.logorrr.util.{ClipBoardUtils, JfxUtils}
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color

import java.time.Instant
import scala.jdk.CollectionConverters._

object LogTextView {

  val timeBarColor = Color.BISQUE.darker()

  val timeBarOverflowColor = timeBarColor.darker()

}


class LogTextView(pathAsString: String
                  , filteredList: FilteredList[LogEntry]
                  , timings: Map[Int, Instant]
                 ) extends BorderPane {

  private val fixedCellSize = 26


  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength = filteredList.size().toString.length

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.getStyleClass.add("dense")
    lv.setItems(filteredList)
    val i = LogoRRRGlobals.getLogFileSettings(pathAsString).selectedIndexProperty.get()
    lv.getSelectionModel.select(i)
    lv
  }
  //listView.heightProperty().addListener(JfxUtils.onNew((s: Number) => {}))
  // listView.heightProperty().addListener(JfxUtils.onNew((n: Number) => {  }))
  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())
  //  listView.setFixedCellSize(fixedCellSize)

  LogoRRRGlobals.getLogFileSettings(pathAsString).selectedIndexProperty.addListener(JfxUtils.onNew((n: Number) => {
    Option(listView.getItems.filtered((t: LogEntry) => t.lineNumber == n.intValue()).get(0)) match {
      case Some(value) =>
        val relativeIndex = listView.getItems.indexOf(value)
        listView.getSelectionModel.select(relativeIndex)
        listView.scrollTo(relativeIndex - ((listView.getHeight / fixedCellSize) / 2).toInt)
      // println(s"selectedIndex : ${n.intValue()}, scrollTo : $relativeIndex")
      case None =>
    }

  }))

  setCenter(listView)


  /** scroll to end of listview */
  def scrollToEnd(): Unit = {
    listView.scrollTo(listView.getItems.size)
  }

  class LogEntryListCell extends ListCell[LogEntry] {
    styleProperty().bind(LogoRRRGlobals.getLogFileSettings(pathAsString).fontStyle)
    //setStyle(LogoRRRFonts.jetBrainsMono(LogTextView.fontSize))
    setGraphic(null)
    val cm = new ContextMenu()
    val copyCurrentToClipboard = new MenuItem("copy text to clipboard")

    cm.getItems.add(copyCurrentToClipboard)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          setText(null)
          val filters = LogoRRRGlobals.getLogFileSettings(pathAsString).filtersProperty.get().asScala.toSeq
          val entry = LogTextViewLabel(e, maxLength, filters)
          entry.lineNumberLabel.styleProperty().bind(LogoRRRGlobals.getLogFileSettings(pathAsString).fontStyle)
          entry.labels.foreach(l => l.styleProperty().bind(LogoRRRGlobals.getLogFileSettings(pathAsString).fontStyle))
          setGraphic(entry)
          copyCurrentToClipboard.setOnAction(_ => ClipBoardUtils.copyToClipboardText(e.value))
          setContextMenu(cm)
        case None =>
          setGraphic(null)
          setText(null)
          setContextMenu(null)
      }
    }

  }
}


