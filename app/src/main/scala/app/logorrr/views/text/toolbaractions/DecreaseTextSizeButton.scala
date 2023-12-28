package app.logorrr.views.text.toolbaractions

import app.logorrr.io.FileId
import app.logorrr.views.ops.TextSizeButton
import app.logorrr.views.search.OpsToolBar

class DecreaseTextSizeButton(val fileId: FileId) extends TextSizeButton(9, "decrease text size") {

  setOnAction(_ => {
    if (getFontSize - OpsToolBar.fontSizeStep > 0) {
      setFontSize(getFontSize - OpsToolBar.fontSizeStep)
    }
  })
}
