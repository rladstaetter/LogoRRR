package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.GfxElements
import javafx.beans.binding.ObjectBinding
import javafx.scene.Node
import javafx.scene.control.{ToggleButton, Tooltip}

object AddToGlobalSearchTermGroupListButton extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[AddToGlobalSearchTermGroupListButton])
}

case class AddToGlobalSearchTermGroupListButton(fileId: FileId) extends ToggleButton {
  setId(AddToGlobalSearchTermGroupListButton.uiNode(fileId).value)
  setTooltip(new Tooltip("share globally"))

  graphicProperty().bind(new ObjectBinding[Node] {
    bind(selectedProperty)
    override def computeValue(): Node = {
      if (isSelected) {
        GfxElements.heartDarkIcon
      } else {
        GfxElements.heartIcon
      }
    }
  })

}