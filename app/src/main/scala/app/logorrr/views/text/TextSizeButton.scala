package app.logorrr.views.text

import app.logorrr.util.{ColorUtil, LogoRRRFonts}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{Button, Label}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.paint.Color

/**
 * UI element for changing text size
 *
 * @param size size of 'T' icon
 * @param eventHandler event which will be triggered upon click
 */
class TextSizeButton(size: Int
                     , eventHandler: EventHandler[ActionEvent]) extends Button {
  val l = new Label("T")
  l.setStyle(LogoRRRFonts.jetBrainsMono(size))
  l.setTextFill(Color.DARKGREY)
  setGraphic(l)
  setOnAction(eventHandler)
  setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      fire()
    }
  })
}
