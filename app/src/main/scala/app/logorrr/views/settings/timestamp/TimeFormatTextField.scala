package app.logorrr.views.settings.timestamp

import app.logorrr.io.FileId
import app.logorrr.model.TimestampSettings
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField

object TimeFormatTextField extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimeFormatTextField])
}

class TimeFormatTextField(fileId: FileId) extends TextField {
  setId(TimeFormatTextField.uiNode(fileId).value)
  setPromptText("<enter time format>")
  setText(TimestampSettings.DefaultPattern)
  setPrefColumnCount(30)
}
