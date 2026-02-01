package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ObjectPropertyBase, Property}
import javafx.geometry.Pos
import javafx.scene.control.Label

object FromLabel extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[FromLabel])

class FromLabel extends Label("from column")
  with BoundFileId(f => FromLabel.uiNode(f).value):
  setPrefWidth(100)
  setAlignment(Pos.CENTER_LEFT)

