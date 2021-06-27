package app.logorrr.views

import app.logorrr.{LogEntry, LogoRRRFonts}
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import javafx.scene.input.{Clipboard, ClipboardContent}
import javafx.scene.layout.BorderPane

import java.time.Instant
import scala.collection.mutable

object ClipBoardUtils {

  def copyToClipboardText(s: String): Unit = {
    val clipboard = Clipboard.getSystemClipboard()
    val content = new ClipboardContent()
    content.putString(s)
    clipboard.setContent(content)
  }
}


object LogColumnDef {

  private val Year = "Year"
  private val Month = "Month"
  private val Day = "Day"
  private val Hour = "Hour"
  private val Minute = "Minute"
  private val Second = "Second"
  private val Millisecond = "Millisecond"

  /** entries to be defined in log format learner */
  val entries = Seq(Year, Month, Day, Hour, Minute, Second, Millisecond)


  case class SimpleRange(start: Int, end: Int) {
    require(start <= end)
    val length = end - start
  }

  /**
   * able to parse following string:
   *
   * 2020-08-11 07:17:43.962
   *
   * */
  val Default =
    LogColumnDef(
      SimpleRange(1, 5)
      , SimpleRange(6, 8)
      , SimpleRange(9, 11)
      , SimpleRange(12, 14)
      , SimpleRange(15, 17)
      , SimpleRange(18, 20)
      , SimpleRange(21, 24))

  def apply(): LogColumnDef = Default

  def apply(map: mutable.Map[String, LogColumnDef.SimpleRange]): LogColumnDef = {
    LogColumnDef(
      map(Year)
      , map(Month)
      , map(Day)
      , map(Hour)
      , map(Minute)
      , map(Second)
      , map(Millisecond)
    )
  }
}

case class LogColumnDef(yearRange: LogColumnDef.SimpleRange
                        , monthRange: LogColumnDef.SimpleRange
                        , dayRange: LogColumnDef.SimpleRange
                        , hourRange: LogColumnDef.SimpleRange
                        , minuteRange: LogColumnDef.SimpleRange
                        , secondRange: LogColumnDef.SimpleRange
                        , milliRange: LogColumnDef.SimpleRange) {

  require(yearRange.length == 4)
  require(monthRange.length == 2)
  require(dayRange.length == 2)
  require(hourRange.length == 2)
  require(minuteRange.length == 2)
  require(secondRange.length == 2)
  require(milliRange.length == 3)

  def substring(value: String, range: LogColumnDef.SimpleRange): String = {
    value.substring(range.start, range.end)
  }

  def parse(logEntryAsString: String): Instant = {
    val year = substring(logEntryAsString, yearRange)
    val month = substring(logEntryAsString, monthRange)
    val day = substring(logEntryAsString, dayRange)
    val hour = substring(logEntryAsString, hourRange)
    val minute = substring(logEntryAsString, minuteRange)
    val second = substring(logEntryAsString, secondRange)
    val milli = substring(logEntryAsString, milliRange)
    Instant.parse(s"$year-$month-${day}T$hour:$minute:$second.${milli}Z")
  }


}

class LogTextView(filteredList: FilteredList[LogEntry]) extends BorderPane {

  val logColumnDefProperty = new SimpleObjectProperty[LogColumnDef]()

  def setLogColumnDef(value: LogColumnDef): Unit = logColumnDefProperty.set(value)

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(filteredList)
    lv
  }

  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())

  setCenter(listView)

  class LogEntryListCell extends ListCell[LogEntry] {

    setStyle(LogoRRRFonts.jetBrainsMono(12))
    setGraphic(null)
    val cm = new ContextMenu()
    val copyCurrentToClipboard = new MenuItem("copy to clipboard")
    //val learnLogFormat = new MenuItem("learn logformat")
    cm.getItems.addAll(copyCurrentToClipboard)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          setText(e.value)
          copyCurrentToClipboard.setOnAction(_ => {
            ClipBoardUtils.copyToClipboardText(e.value)
          })
          /*
          learnLogFormat.setOnAction(_ => {
            val stage = LogFormatLearnerStage(e)
            stage.showAndWait()
            setLogColumnDef(stage.getLogColumnDef())
          })
          */
          setContextMenu(cm)
        case None =>
          setText(null)
          setContextMenu(null)
      }
    }
  }


  def selectEntryByIndex(index: Int): Unit = {
    listView.getSelectionModel.select(index)
    listView.scrollTo(index)
  }

}


