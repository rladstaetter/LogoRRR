package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


class StopTimeAnimationButton(mutLogFileSettings: MutLogFileSettings
                              , replayButton: ReplayButton) extends Button {

  // set visible only if we have a valid timestamp setting
  visibleProperty().bind(mutLogFileSettings.hasTimestampSetting)

  setGraphic(new FontIcon(FontAwesomeRegular.STOP_CIRCLE))
  setTooltip(new Tooltip("stop animation"))
  setOnAction(_ => {
    replayButton.stopTimer()
  })
}