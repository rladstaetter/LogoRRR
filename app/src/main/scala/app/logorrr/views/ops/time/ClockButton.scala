package app.logorrr.views.ops.time

import app.logorrr.conf.TimestampSettings
import app.logorrr.views.settings.timestamp.TimestampSettingStage
import javafx.scene.control.{Button, Tooltip}
import javafx.stage.Window
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

class ClockButton(stage: TimestampSettingStage) extends Button:
  setBackground(null)
  setStyle(
    """|-color-button-bg: -color-bg-subtle;
       |-fx-background-insets: 0;
       |""".stripMargin)
  setGraphic(new FontIcon(FontAwesomeRegular.CLOCK))
  setTooltip(new Tooltip("configure time format"))
  setOnAction(_ => stage.showAndWait())

  def init(someGlobalTimestampSettings: Option[TimestampSettings]
           , someLocalTimestampSettings: Option[TimestampSettings]
           , owner: Window): Unit =
    stage.init(someGlobalTimestampSettings, someLocalTimestampSettings, owner)

  def shutdown(): Unit =
    stage.shutdown()