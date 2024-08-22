package app.logorrr.views.settings.timestamp

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.geometry.Pos
import javafx.scene.control.Label

object FromLabel extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[FromLabel])
}

class FromLabel(id: FileId) extends Label("from column") {
  setId(FromLabel.uiNode(id).value)
  setPrefWidth(100)
  setAlignment(Pos.CENTER_RIGHT)
}