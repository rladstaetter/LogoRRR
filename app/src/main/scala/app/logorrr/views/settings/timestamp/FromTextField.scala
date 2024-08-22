package app.logorrr.views.settings.timestamp

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField

object FromTextField extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[FromTextField])
}

class FromTextField(id: FileId) extends TextField {
  setId(FromTextField.uiNode(id).value)
  setPrefWidth(60)
  setEditable(false)
}