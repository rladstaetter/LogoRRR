package app.logorrr.views.settings.timestamp

import app.logorrr.conf.{FileId, TimeSettings}
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField

object TimeFormatTextField extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimeFormatTextField])

class TimeFormatTextField extends TextField with BoundId(TimeFormatTextField.uiNode(_).value):

  setPromptText(s"enter time format pattern, e.g: ${TimeSettings.DefaultPattern}")
  setText("")
  setPrefColumnCount(30)
