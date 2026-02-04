package app.logorrr.views.settings.timestamp

import app.logorrr.conf.{FileId, TimestampSettings}
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField

object TimeFormatTextField extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimeFormatTextField])

class TimeFormatTextField extends TextField with BoundFileId(TimeFormatTextField.uiNode(_).value):

  setPromptText(s"enter time format pattern, e.g: ${TimestampSettings.DefaultPattern}")
  setText("")
  setPrefColumnCount(30)
