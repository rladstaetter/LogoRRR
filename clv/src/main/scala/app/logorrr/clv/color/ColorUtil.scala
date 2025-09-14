package app.logorrr.clv.color

import javafx.scene.paint.Color

/**
 * Utility to manage some operations dealing with color
 */
object ColorUtil {

  def toARGB(color: Color): Int =
    (color.getOpacity * 255).toInt << 24 |
      (color.getRed * 255).toInt << 16 |
      (color.getGreen * 255).toInt << 8 |
      (color.getBlue * 255).toInt

  def intToColor(rgba: Int): Color = {
    val red = ((rgba >> 24) & 0xFF) / 255.0
    val green = ((rgba >> 16) & 0xFF) / 255.0
    val blue = ((rgba >> 8) & 0xFF) / 255.0
    val alpha = (rgba & 0xFF) / 255.0
    new Color(red, green, blue, alpha)
  }

  def toCssString(color: Color): String = {
    // Get the hexadecimal representation of the color
    // The format is "#RRGGBB"
    String.format("#%02X%02X%02X", (color.getRed * 255).asInstanceOf[Int], (color.getGreen * 255).asInstanceOf[Int], (color.getBlue * 255).asInstanceOf[Int])
  }

  def mkCssBackgroundString(color: Color): String = {
    "-fx-background-color: " + ColorUtil.toCssString(color) + ";"
  }



}
