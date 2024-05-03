package app.logorrr.usecases.dnd

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.Button
import javafx.scene.input.{ClipboardContent, TransferMode}

import java.nio.file.Path
import java.util.Collections

object DragSourceButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DragSourceButton])

}

class DragSourceButton(p: Path) extends Button(p.getFileName.toString) {
  setId(DragSourceButton.uiNode(FileId(p)).value)

  setOnDragDetected(_ => {
    val content = new ClipboardContent()
    content.putFiles(Collections.singletonList(p.toFile))
    startFullDrag()
    startDragAndDrop(TransferMode.ANY: _*).setContent(content)
  })

}