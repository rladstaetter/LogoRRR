package app.logorrr.util

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

import scala.util.Random

/**
 * Utility to manage some operations dealing with color
 */
object ColorUtil {

  def mkR(width: Int, height: Int, color: Color = ColorUtil.randColor): Rectangle = {
    val r = new Rectangle(width, height)
    r.setFill(color)
    r.setStroke(Color.WHITE)
    r
  }

  def toARGB(color: Color): Int =
    (color.getOpacity * 255).toInt << 24 |
      (color.getRed * 255).toInt << 16 |
      (color.getGreen * 255).toInt << 8 |
      (color.getBlue * 255).toInt

  def randColor = Color.color(Random.nextDouble()
    , Random.nextDouble()
    , Random.nextDouble()
  )


}
