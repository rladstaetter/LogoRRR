package app.logorrr.views.ops.time

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

object StopTimeAnimationButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StopTimeAnimationButton])

}

class StopTimeAnimationButton(mutLogFileSettings: MutLogFileSettings
                              , replayStackPane: ReplayStackPane) extends Button {

  setId(StopTimeAnimationButton.uiNode(mutLogFileSettings.getFileId).value)

  // set visible only if we have a valid timestamp setting
  visibleProperty().bind(mutLogFileSettings.hasTimestampSetting)

  setGraphic(new FontIcon(FontAwesomeRegular.STOP_CIRCLE))
  setTooltip(new Tooltip("stop animation"))
  setOnAction(_ => {
    replayStackPane.reset()
  })
}