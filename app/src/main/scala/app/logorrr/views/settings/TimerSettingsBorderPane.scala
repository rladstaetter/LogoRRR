package app.logorrr.views.settings

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import app.logorrr.util.{CanLog, HLink}
import javafx.collections.ObservableList
import javafx.scene.control._
import javafx.scene.layout.BorderPane

import scala.util.Try


object TimerSettingsBorderPane {

  val dateTimeFormatterLink = HLink("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html", "format description")

  def mkTf(name: String
           , somePrompt: Option[String]
           , columnCount: Int): (Label, TextField) = {
    val l = new Label(name)
    val tf = new TextField()
    somePrompt.foreach(pt => tf.setPromptText(pt))
    tf.setPrefColumnCount(columnCount)
    (l, tf)
  }
}

class TimerSettingsBorderPane(pathAsString: String
                              , logEntriesToDisplay: ObservableList[LogEntry]
                              , updateLogEntrySetting: LogEntryInstantFormat => Unit
                              , closeStage: => Unit)
  extends BorderPane with CanLog {

  val timerSettingsLogTextView = new TimerSettingsLogView(pathAsString, logEntriesToDisplay)

  val (startColLabel, startColTf) = TimerSettingsBorderPane.mkTf("start column", None, 4)
  val (timeFormatLabel, timeFormatTf) = TimerSettingsBorderPane.mkTf("time format", Option("<enter time format>"), 30)
  val (endColLabel, endColTf) = TimerSettingsBorderPane.mkTf("end column", None, 4)

  /**
   * if ok button is clicked, log definition will be written, settings stage will be closed, associated logfile
   * definition will be updated
   * */
  private val okButton = {
    val b = new Button("set new format")
    b.setOnAction(_ => {
      updateLogEntrySetting(mkLogEntrySetting)
      closeStage
    })
    b
  }

  private val cancelButton = {
    val b = new Button("cancel")
    b.setOnAction(_ => closeStage)
    b
  }

  private val hyperlink: Hyperlink = TimerSettingsBorderPane.dateTimeFormatterLink.mkHyperLink()

  private val bar = {
    val tb = new ToolBar()
    tb.getItems.addAll(
      startColLabel, startColTf
      , endColLabel, endColTf
      , timeFormatLabel, timeFormatTf, hyperlink
      , okButton)
    tb
  }

  LogoRRRGlobals.getLogFileSettings(pathAsString).someLogEntrySettings.get() match {
    case Some(s) =>
      startColTf.setText(s.startColumn.start.toString)
      endColTf.setText(s.startColumn.end.toString)
      timeFormatTf.setText(s.dateTimePattern)
    case None =>
      logTrace("No time setting found ... ")
  }

  setCenter(timerSettingsLogTextView)
  setBottom(bar)

  def mkLogEntrySetting: LogEntryInstantFormat = {
    val timeFormat = timeFormatTf.getText.trim
    val start = Try(startColTf.getText.trim.toInt).getOrElse(0)
    val end = Try(endColTf.getText.trim.toInt).getOrElse(1)
    LogEntryInstantFormat(SimpleRange(start, end), timeFormat)
  }
}