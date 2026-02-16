package app.logorrr.views.search.stg

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.util.GfxElements
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.Button
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


object CreateStgButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CreateStgButton])

class CreateStgButton extends Button("") with BoundId(CreateStgButton.uiNode(_).value) {
  
  setGraphic(GfxElements.Icons.plusSquare)

  
  def init(fileProperty: ObjectPropertyBase[FileId]): Unit =
    bindIdProperty(fileProperty)

  def shutdown(): Unit =
    unbindIdProperty()
}