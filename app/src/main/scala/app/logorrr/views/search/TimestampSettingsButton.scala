package app.logorrr.views.search

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.settings.timestamp.TimestampSettingStage
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.layout.StackPane
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

/**
 * Displays a clock in the ops tool bar, with a red exclamation mark if there is no setting for the
 * timestamp format for the given log file.
 *
 * Given a time stamp format (for example: YYYY-MM-dd HH:mm:ss.SSS), LogoRRR is able to parse the timestamp
 * and thus has new possibilities to analyse the log file.
 *
 * @param settings            settings for specific log file
 * @param logEntriesToDisplay the list of log entries to display in order to configure a time format
 */
class TimestampSettingsButton(settings: MutLogFileSettings
                              , logEntriesToDisplay: ObservableList[LogEntry]) extends StackPane {

  // since timerbutton is a stackpane, this css commands are necessary to have the same effect as
  // defined in primer-light.css
  val button: Button = {
    val btn = new Button()
    btn.setBackground(null)
    btn.setStyle(
      """
        |-color-button-bg: -color-bg-subtle;
        |-fx-background-insets: 0;
        |""".stripMargin)
    btn.setGraphic(new FontIcon(FontAwesomeRegular.CLOCK))
    btn.setTooltip(new Tooltip("configure time format"))
    btn.setOnAction(_ => new TimestampSettingStage(settings, logEntriesToDisplay).showAndWait())
    btn
  }

  private val fontIcon = {
    val icon = new FontIcon()
    icon.setStyle("-fx-icon-code:fas-exclamation-circle;-fx-icon-color:rgba(255, 0, 0, 1);-fx-icon-size:8;")
    icon.setTranslateX(10)
    icon.setTranslateY(-10)
    /** red exclamation mark is only visible if there is no timestamp setting for a given log file */
    icon.visibleProperty().bind(settings.hasLogEntrySettingBinding.not())
    icon
  }

  getChildren.addAll(button, fontIcon)

}
