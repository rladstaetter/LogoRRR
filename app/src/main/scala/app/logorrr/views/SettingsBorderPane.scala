package app.logorrr.views

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntryInstantFormat
import app.logorrr.util.HLink
import javafx.scene.control._
import javafx.scene.layout.BorderPane

import scala.util.Try


object SettingsBorderPane {

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

class SettingsBorderPane(pathAsString: String
                         , updateLogEntrySetting: LogEntryInstantFormat => Unit
                         , closeStage: => Unit) extends BorderPane {

  val (startColLabel, startColTf) = SettingsBorderPane.mkTf("start column", None, 4)
  val (timeFormatLabel, timeFormatTf) = SettingsBorderPane.mkTf("time format", Option("<enter time format>"), 30)
  val (endColLabel, endColTf) = SettingsBorderPane.mkTf("end column", None, 4)

  /**
   * if ok button is clicked, log definition will be written, settings stage will be closed, associated logfile
   * definition will be updated
   * */
  private val okButton = {
    val b = new Button("ok")
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

  private val hyperlink: Hyperlink = SettingsBorderPane.dateTimeFormatterLink.mkHyperLink()

  private val bar = {
    val tb = new ToolBar()
    tb.getItems.addAll(startColLabel, startColTf
      , timeFormatLabel, timeFormatTf
      , endColLabel, endColTf
      , okButton, cancelButton
      , hyperlink)
    tb
  }

  LogoRRRGlobals.getLogFileSettings(pathAsString).someLogEntrySettings.get().foreach(s => {
    startColTf.setText(s.dateTimeRange.start.toString)
    endColTf.setText(s.dateTimeRange.end.toString)
    timeFormatTf.setText(s.dateTimePattern)
  })


  setCenter(bar)


  def mkLogEntrySetting: LogEntryInstantFormat = {
    val timeFormat = timeFormatTf.getText.trim
    val start = Try(startColTf.getText.trim.toInt).getOrElse(0)
    val end = Try(endColTf.getText.trim.toInt).getOrElse(1)
    LogEntryInstantFormat(SimpleRange(start, end), timeFormat)
  }
}