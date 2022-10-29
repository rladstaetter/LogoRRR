package app.logorrr.views.ops

import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.text.HasFontSizeProperty
import javafx.scene.control.{Button, Label, Tooltip}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.paint.Color

abstract class TextSizeButton(size: Int, tooltipMessage: String)
  extends Button
    with HasFontSizeProperty {

  private val label = new Label("T")
  label.setStyle(LogoRRRFonts.jetBrainsMono(size))
  label.setTextFill(Color.DARKGREY)
  setGraphic(label)
  setTooltip(new Tooltip(tooltipMessage))
  setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      fire()
    }
  })
}
