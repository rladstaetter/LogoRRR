package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.Button


object CreateStgButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CreateStgButton])
}

case class CreateStgButton(fileId: FileId) extends Button("Create") {
  setId(CreateStgButton.uiNode(fileId).value)
}