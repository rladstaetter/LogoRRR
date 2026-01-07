package app.logorrr.usecases.dnd

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.Button
import javafx.scene.input.{ClipboardContent, TransferMode}

import java.util.Collections

object DragSourceButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DragSourceButton])

}

class DragSourceButton(fileId: FileId) extends Button(fileId.fileName) {
  setId(DragSourceButton.uiNode(fileId).value)

  setOnDragDetected(_ => {
    val content = new ClipboardContent()
    content.putFiles(Collections.singletonList(fileId.asPath.toFile))
    startFullDrag()
    startDragAndDrop(TransferMode.ANY: _*).setContent(content)
  })

}