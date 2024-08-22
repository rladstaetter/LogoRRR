package app.logorrr.views.settings.timestamp

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.geometry.Pos
import javafx.scene.control.Label

object ToLabel extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[ToLabel])
}

class ToLabel(id: FileId) extends Label("to column:") {
  setId(FromLabel.uiNode(id).value)
  setPrefWidth(100)
  setAlignment(Pos.CENTER_RIGHT)
}