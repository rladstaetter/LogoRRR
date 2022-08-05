package app.logorrr.views.block

import app.logorrr.util.ColorUtil
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.Button
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.paint.Color

/**
 * A button which contains a rectangle of given color and size.
 *
 * @param width width of rectangle
 * @param height height of rectangle
 * @param color color of rectangle
 * @param eventHandler
 */
class RectButton(width: Int
                 , height: Int
                 , color: Color
                 , eventHandler: EventHandler[ActionEvent]) extends Button {
  setGraphic(ColorUtil.mkR(width, height, color))
  setOnAction(eventHandler)
  setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      fire()
    }
  })
}
