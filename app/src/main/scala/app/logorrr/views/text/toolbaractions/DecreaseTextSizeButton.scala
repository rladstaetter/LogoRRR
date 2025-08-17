package app.logorrr.views.text.toolbaractions

import app.logorrr.io.FileId
import app.logorrr.views.logfiletab.TextConstants
import app.logorrr.views.ops.TextSizeButton
import app.logorrr.views.{UiNode, UiNodeFileIdAware}

object DecreaseTextSizeButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DecreaseTextSizeButton])

}

class DecreaseTextSizeButton(val fileId: FileId) extends TextSizeButton(9, "decrease text size") {

  setId(DecreaseTextSizeButton.uiNode(fileId).value)
  setOnAction(_ => {
    if (getFontSize - TextConstants.fontSizeStep > 0) {
      setFontSize(getFontSize - TextConstants.fontSizeStep)
    }
  })
}
