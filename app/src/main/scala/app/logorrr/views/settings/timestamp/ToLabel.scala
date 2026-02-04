package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.geometry.Pos
import javafx.scene.control.Label

object ToLabel extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[ToLabel])

class ToLabel extends ALabel("to column",ToLabel.uiNode(_).value)
