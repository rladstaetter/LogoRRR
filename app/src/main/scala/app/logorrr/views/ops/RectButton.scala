package app.logorrr.views.ops

import app.logorrr.util.ColorUtil
import javafx.scene.control.{Button, Tooltip}
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
abstract class RectButton(width: Int
                          , height: Int
                          , color: Color
                          , tooltipMessage: String) extends Button {

  setTooltip(new Tooltip(tooltipMessage))
  setGraphic(ColorUtil.mkR(width, height, color))
  setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      fire()
    }
  })
}
