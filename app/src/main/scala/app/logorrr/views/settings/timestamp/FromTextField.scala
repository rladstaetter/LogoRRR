package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField

object FromTextField extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[FromTextField])

class FromTextField extends ATextField(FromTextField.uiNode(_).value)



