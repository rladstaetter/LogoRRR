package app.logorrr.views.ops

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

object RectButton {

  def mkR(width: Int, height: Int, color: Color): Rectangle = {
    val r = new Rectangle(width, height)
    r.setFill(color)
    r.setStroke(Color.WHITE)
    r
  }

}

/**
 * A button which contains a rectangle of given color and size.
 *
 * @param width          width of rectangle
 * @param height         height of rectangle
 * @param color          color of rectangle
 * @param tooltipMessage message which is displayed if you hover over this ui element
 */
abstract class RectButton(width: Int
                          , height: Int
                          , color: Color
                          , tooltipMessage: String)
  extends SizeButton(RectButton.mkR(width, height, color), tooltipMessage)

