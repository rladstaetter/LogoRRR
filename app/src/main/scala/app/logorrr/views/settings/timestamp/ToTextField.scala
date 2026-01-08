package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField

object ToTextField extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[FromTextField])

class ToTextField(id: FileId) extends TextField {
  setId(ToTextField.uiNode(id).value)
  setPrefWidth(60)
  setEditable(false)
}