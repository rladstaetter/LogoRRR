package app.logorrr.views.ops

import javafx.scene.paint.Color

/**
 * A button where height and width are the same
 *
 * @param size           square length
 * @param color          color of rectangle
 * @param tooltipMessage tooltip message if you hover over this ui element
 */
abstract class SquareButton(size: Int
                            , color: Color
                            , tooltipMessage: String)
  extends RectButton(size, size, color, tooltipMessage)
