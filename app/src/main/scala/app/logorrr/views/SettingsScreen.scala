package app.logorrr.views

import app.logorrr.conf.{Settings, SettingsIO}
import app.logorrr.model.{LogEntry, LogEntrySetting, LogReportDefinition}
import app.logorrr.util.HLink
import javafx.application.HostServices
import javafx.geometry.Insets
import javafx.scene.control.{Button, Hyperlink, Label, TextField, ToolBar}
import javafx.scene.layout.BorderPane

import scala.util.Try


object SettingsScreen {

  val dateTimeFormatterLink = HLink("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html", "Format description")

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

case class SettingsScreen(hostServices: HostServices
                          , lrd: LogReportDefinition) extends BorderPane {

  val (startColLabel, startColTf) = SettingsScreen.mkTf("start column", None, 4)
  val (timeFormatLabel, timeFormatTf) = SettingsScreen.mkTf("time format", Option("<enter time format>"), 30)
  val (endColLabel, endColTf) = SettingsScreen.mkTf("end column", None, 4)

  private val okButton = new Button("ok")
  okButton.setOnAction(_ => {
    val timeFormat = timeFormatTf.getText.trim
    val start = Try(startColTf.getText.trim.toInt).getOrElse(0)
    val end = Try(endColTf.getText.trim.toInt).getOrElse(1)

    val updatedLogReportDefinition = lrd.copy(someLogEntrySetting = Option(LogEntrySetting(SimpleRange(start, end), timeFormat)))

    SettingsIO.updateRecentFileSettings(rf => rf.update(updatedLogReportDefinition))

  })

  private val cancelButton = new Button("cancel")
  private val hyperlink: Hyperlink = SettingsScreen.dateTimeFormatterLink.mkHyperLink(hostServices)

  lrd.someLogEntrySetting.foreach(s => timeFormatTf.setText(s.dateTimePattern))

  private val bar = {
    val tb = new ToolBar()
    tb.getItems.addAll(startColLabel, startColTf
      , timeFormatLabel, timeFormatTf
      , endColLabel, endColTf
      , okButton, cancelButton
      , hyperlink)
    tb
  }

  setCenter(bar)

}