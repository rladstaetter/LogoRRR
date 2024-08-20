package app.logorrr.views.ops.time

import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


class StopTimeAnimationButton(replayButton: ReplayButton) extends Button {
  setGraphic(new FontIcon(FontAwesomeRegular.STOP_CIRCLE))
  setTooltip(new Tooltip("stop animation"))
  setOnAction(_ => {
    replayButton.stopTimer()
  })
}