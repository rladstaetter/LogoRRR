package app.logorrr.views.text.toolbaractions

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.ops.TextSizeButton
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.{UiNode, UiNodeAware}

object IncreaseTextSizeButton extends UiNodeAware {

  override def uiNode(id: FileId): UiNode = UiNode("increasetextsizebutton-" + HashUtil.md5Sum(id))
}


/**
 * UI element for changing text size
 *
 * @param fileId to resolve current log file
 */
class IncreaseTextSizeButton(val fileId: FileId) extends TextSizeButton(16, "increase text size") {
  setId(IncreaseTextSizeButton.uiNode(fileId).value)
  setOnAction(_ => {
    if (getFontSize + OpsToolBar.fontSizeStep < 50 * OpsToolBar.fontSizeStep) {
      setFontSize(getFontSize + OpsToolBar.fontSizeStep)
    }
  })
}



