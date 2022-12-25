package app.logorrr.views.text

import app.logorrr.views.ops.TextSizeButton
import app.logorrr.views.search.OpsToolBar

class DecreaseTextSizeButton(val pathAsString: String) extends TextSizeButton(9, "decrease text size") {

  setOnAction(_ => {
    if (getFontSize() - OpsToolBar.fontSizeStep > 0) {
      setFontSize(getFontSize() - OpsToolBar.fontSizeStep)
    }
  })
}
