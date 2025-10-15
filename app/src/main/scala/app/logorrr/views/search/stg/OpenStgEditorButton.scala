package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


object OpenStgEditorButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[OpenStgEditorButton])

}

case class OpenStgEditorButton(fileId: FileId, addFn: String => Unit) extends Button {
  setId(OpenStgEditorButton.uiNode(fileId).value)
  setGraphic(new FontIcon(FontAwesomeRegular.EDIT))
  setTooltip(new Tooltip("edit search term groups"))
  setOnAction(_ => new SearchTermGroupEditor(this.getScene.getWindow, fileId, addFn).showAndWait())


}
