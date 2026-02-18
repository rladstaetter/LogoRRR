package app.logorrr.clv.color

import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.paint.CycleMethod.NO_CYCLE
import javafx.scene.paint.{Color, LinearGradient, Paint, Stop}

/**
 * Utility to manage some operations dealing with color
 */
object ColorUtil:

  /** creates a background with given paint */
  def mkBg(paint: Paint) = new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY))

  /**
   * If luminance is high, return a darker color; otherwise, return a brighter one
   *
   * @param color color to analyze
   * @return
   */
  def getContrastColor(color: Color): Color = {
    // Formula for relative luminance
    val luminance = (0.299 * color.getRed) + (0.587 * color.getGreen) + (0.114 * color.getBlue)
    // If luminance is high, return black; otherwise, return white
    if (luminance > 0.5) Color.BLACK
    else Color.WHITE
  }

  val gradientBackground: Background =
    val stops = Seq(
      new Stop(1, Color.web("#ffffff")),
      new Stop(0, Color.BEIGE)
    )
    val backgroundGradient = new LinearGradient(0, 0, 0, 1, true, NO_CYCLE, stops *)
    ColorUtil.mkBg(backgroundGradient)


  def toARGB(color: Color): Int =
    (color.getOpacity * 255).toInt << 24 |
      (color.getRed * 255).toInt << 16 |
      (color.getGreen * 255).toInt << 8 |
      (color.getBlue * 255).toInt

  def intToColor(rgba: Int): Color =
    val red = ((rgba >> 24) & 0xFF) / 255.0
    val green = ((rgba >> 16) & 0xFF) / 255.0
    val blue = ((rgba >> 8) & 0xFF) / 255.0
    val alpha = (rgba & 0xFF) / 255.0
    new Color(red, green, blue, alpha)

  def hexString(color: Color): String =
    // Get the hexadecimal representation of the color
    // The format is "#RRGGBB"
    String.format("#%02X%02X%02X", (color.getRed * 255).asInstanceOf[Int], (color.getGreen * 255).asInstanceOf[Int], (color.getBlue * 255).asInstanceOf[Int])

  def cssLinearGradient(direction: String, from: Color, to: Color): String =
    s"""linear-gradient($direction, ${hexString(from)}, ${hexString(to)})"""



