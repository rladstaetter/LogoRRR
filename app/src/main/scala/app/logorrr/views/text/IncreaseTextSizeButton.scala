package app.logorrr.views.text

import app.logorrr.views.ops.TextSizeButton
import app.logorrr.views.search.OpsToolBar

/**
 * UI element for changing text size
 *
 * @param size size of 'T' icon
 * @param eventHandler event which will be triggered upon click
 */
class IncreaseTextSizeButton(val pathAsString: String) extends TextSizeButton(9, "increase text size") {

  setOnAction(_ => {
    if (getFontSize() + OpsToolBar.fontSizeStep < 10 * OpsToolBar.fontSizeStep) {
      setFontSize(getFontSize() + OpsToolBar.fontSizeStep)
    }
  })
}


class DecreaseTextSizeButton(val pathAsString: String) extends TextSizeButton(6, "decrease text size") {

  setOnAction(_ => {
    if (getFontSize() - OpsToolBar.fontSizeStep > 0) {
      setFontSize(getFontSize() - OpsToolBar.fontSizeStep)
    }
  })
}
