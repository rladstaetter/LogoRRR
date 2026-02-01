package app.logorrr.views.search.stg

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.Button
import javafx.stage.Stage


object CloseStgEditorButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseStgEditorButton])

class CloseStgEditorButton(fileId: FileId, stage: Stage) extends Button("Apply & close") {
  setId(CloseStgEditorButton.uiNode(fileId).value)
  setOnAction(_ => stage.close())
}