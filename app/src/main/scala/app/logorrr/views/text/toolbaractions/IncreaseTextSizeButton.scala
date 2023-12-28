package app.logorrr.views.text.toolbaractions

import app.logorrr.io.FileId
import app.logorrr.views.ops.TextSizeButton
import app.logorrr.views.search.OpsToolBar

/**
 * UI element for changing text size
 *
 * @param fileId to resolve current log file
 */
class IncreaseTextSizeButton(val fileId: FileId) extends TextSizeButton(16, "increase text size") {

  setOnAction(_ => {
    if (getFontSize + OpsToolBar.fontSizeStep < 50 * OpsToolBar.fontSizeStep) {
      setFontSize(getFontSize + OpsToolBar.fontSizeStep)
    }
  })
}



