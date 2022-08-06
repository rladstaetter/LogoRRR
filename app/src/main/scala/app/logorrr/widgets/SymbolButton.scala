package app.logorrr.widgets

import app.logorrr.util.LogoRRRFonts
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{Button, Label}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.paint.Color

class SymbolButton(size: Int
                   , symbol: Char
                   , eventHandler: EventHandler[ActionEvent]) extends Button {
  private val label = new Label(symbol.toString)
  label.setStyle(LogoRRRFonts.jetBrainsMono(size))
  label.setTextFill(Color.DARKGREY)
  setGraphic(label)
  setOnAction(eventHandler)
  setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      fire()
    }
  })
}
