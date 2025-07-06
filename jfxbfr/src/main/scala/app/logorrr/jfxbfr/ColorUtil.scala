package app.logorrr.jfxbfr

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

  /**
   * calculate a color for this log entry.
   *
   * - either white if no search filter hits
   * - given color if only one hit
   * - a melange of all colors from all hits in all other cases
   * */
  def calcColor(value: String, filters: Seq[MutFilter[_]]): Color = {
    val hits = filters.filter(sf => sf.matches(value))
    val color = {
      if (hits.isEmpty) {
        Color.LIGHTGREY
      } else if (hits.size == 1) {
        hits.head.getColor
      } else {
        val c = hits.tail.foldLeft(hits.head.getColor)((acc, sf) => acc.interpolate(sf.getColor, 0.5))
        c
      }
    }
    color
  }


}
