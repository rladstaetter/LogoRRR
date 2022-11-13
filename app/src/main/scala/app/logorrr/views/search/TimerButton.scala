package app.logorrr.views.search

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import app.logorrr.util.CanLog
import app.logorrr.views.settings.TimerSettingStage
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.layout.StackPane
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


class TimerButton(pathAsString: String
                  , logEntriesToDisplay: ObservableList[LogEntry])
  extends StackPane
    with CanLog {

  def updateLogEntrySetting(leif: LogEntryInstantFormat): Unit = {
    logError("implement update of global log entry format for this log file tab")
    logTrace(leif.toString)
  }

  val button = new Button()
  button.setBackground(null)
  // since timerbutton is a stackpane, this css commands are necessary to have the same effect as
  // defined in primer-light.css
  button.setStyle(
    """
      |-color-button-bg: -color-bg-subtle;
      |-fx-background-insets: 0;
      |""".stripMargin)

  button.setGraphic(new FontIcon(FontAwesomeRegular.CLOCK))
  button.setTooltip(new Tooltip("configure time format"))

  button.setOnAction(_ => {
    val timerSettingStage = new TimerSettingStage(pathAsString, updateLogEntrySetting, logEntriesToDisplay)
    timerSettingStage.showAndWait()
  })

  private val icon = new FontIcon()
  icon.setStyle("-fx-icon-code:fas-exclamation-circle;-fx-icon-color:rgba(255, 0, 0, 1);-fx-icon-size:8;")
  icon.setTranslateX(10)
  icon.setTranslateY(-10)
  icon.managedProperty().bind(LogoRRRGlobals.getLogFileSettings(pathAsString).hasLogEntrySetting.not())

  getChildren.addAll(button, icon)
}
