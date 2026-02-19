package app.logorrr.views.search.stg

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.util.GfxElements
import javafx.scene.control.Button




object DeleteStgButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DeleteStgButton])

  def apply(fileId: FileId): DeleteStgButton =
    new DeleteStgButton(DeleteStgButton.uiNode(fileId))


class DeleteStgButton(uiNode: UiNode) extends Button:
  setId(uiNode.value)
  setTooltip(GfxElements.ToolTips.mkRemove)
  setGraphic(GfxElements.Icons.windowClose)


