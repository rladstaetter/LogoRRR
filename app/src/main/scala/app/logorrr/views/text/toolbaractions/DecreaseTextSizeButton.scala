package app.logorrr.views.text.toolbaractions

import app.logorrr.io.FileId
import app.logorrr.views.ops.TextSizeButton
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.{UiNode, UiNodeAware}

object DecreaseTextSizeButton extends UiNodeAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DecreaseTextSizeButton])

}

class DecreaseTextSizeButton(val fileId: FileId) extends TextSizeButton(9, "decrease text size") {

  setId(DecreaseTextSizeButton.uiNode(fileId).value)
  setOnAction(_ => {
    if (getFontSize - OpsToolBar.fontSizeStep > 0) {
      setFontSize(getFontSize - OpsToolBar.fontSizeStep)
    }
  })
}
