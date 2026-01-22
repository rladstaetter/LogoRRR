package app.logorrr.views.settings.timestamp

import app.logorrr.conf.{FileId, TimestampSettings}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField

object TimeFormatTextField extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimeFormatTextField])

class TimeFormatTextField(fileId: FileId, pattern : String) extends TextField:
  setId(TimeFormatTextField.uiNode(fileId).value)
  setPromptText("<enter time format>")
  setText(pattern)
  setPrefColumnCount(30)
