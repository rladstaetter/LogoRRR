package app.logorrr.views.ops

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

object RectButton:

  def mkR(width: Int, height: Int, color: Color): Rectangle =
    val r = new Rectangle(width, height)
    r.setFill(color)
    r.setStroke(Color.WHITE)
    r


