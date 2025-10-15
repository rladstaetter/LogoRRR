package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.GfxElements
import javafx.scene.control.Button

case class DeleteStgButton(fileId: FileId) extends Button {
  setId(DeleteStgButton.uiNode(fileId).value)
  setTooltip(GfxElements.mkRemoveTooltip)
  setGraphic(GfxElements.closeWindowIcon)
}

object DeleteStgButton extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DeleteStgButton])
}




