package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.geometry.Pos
import javafx.scene.control.Label

object ToLabel extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[ToLabel])

class ToLabel extends Label("to column")
  with BoundFileId(f => ToLabel.uiNode(f).value):
  setPrefWidth(100)
  setAlignment(Pos.CENTER_LEFT)
