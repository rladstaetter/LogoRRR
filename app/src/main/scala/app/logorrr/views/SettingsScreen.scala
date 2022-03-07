package app.logorrr.views

import app.logorrr.conf.{Settings, SettingsIO}
import app.logorrr.model.{LogEntry, LogEntrySetting, LogFileDefinition}
import app.logorrr.util.HLink
import javafx.application.HostServices
import javafx.geometry.Insets
import javafx.scene.control.{Button, Hyperlink, Label, TextField, ToolBar}
import javafx.scene.layout.BorderPane

import scala.util.Try


object SettingsScreen {

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

class SettingsScreen(hostServices: HostServices
                     , lrd: LogFileDefinition
                     , closeStage: => Unit) extends BorderPane {

  val (startColLabel, startColTf) = SettingsScreen.mkTf("start column", None, 4)
  val (timeFormatLabel, timeFormatTf) = SettingsScreen.mkTf("time format", Option("<enter time format>"), 30)
  val (endColLabel, endColTf) = SettingsScreen.mkTf("end column", None, 4)

  private val okButton = {
    val b = new Button("ok")
    b.setOnAction(_ => {
      val timeFormat = timeFormatTf.getText.trim
      val start = Try(startColTf.getText.trim.toInt).getOrElse(0)
      val end = Try(endColTf.getText.trim.toInt).getOrElse(1)

      val updatedLogFileDefinitions = lrd.copy(someLogEntrySetting = Option(LogEntrySetting(SimpleRange(start, end), timeFormat)))

      SettingsIO.updateRecentFileSettings(rf => rf.update(updatedLogFileDefinitions))
      closeStage
    })
    b
  }

  private val cancelButton = {
    val b = new Button("cancel")
    b.setOnAction(_ => {
      closeStage
    })
    b
  }

  private val hyperlink: Hyperlink = SettingsScreen.dateTimeFormatterLink.mkHyperLink(hostServices)

  private val bar = {
    val tb = new ToolBar()
    tb.getItems.addAll(startColLabel, startColTf
      , timeFormatLabel, timeFormatTf
      , endColLabel, endColTf
      , okButton, cancelButton
      , hyperlink)
    tb
  }

  lrd.someLogEntrySetting.foreach(s => {
    startColTf.setText(s.dateTimeRange.start.toString)
    endColTf.setText(s.dateTimeRange.end.toString)
    timeFormatTf.setText(s.dateTimePattern)
  })


  setCenter(bar)

}