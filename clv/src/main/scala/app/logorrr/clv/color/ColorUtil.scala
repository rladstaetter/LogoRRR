package app.logorrr.clv.color

import javafx.scene.paint.Color

/**
 * Utility to manage some operations dealing with color
 */
object ColorUtil {

  /**
   * Generates a complementary color with guaranteed high contrast
   * (opposite brightness) for text readability.
   * * @param backgroundColor The base color (background).
   *
   * @return The font color with high contrast.
   */
  def getOppositeHighContrastColor(backgroundColor: Color): Color = {
    // 1. Get the complementary HUE (shift by 180 degrees)
    val originalHue = backgroundColor.getHue
    val complementaryHue = (originalHue + 180.0) % 360.0

    // 2. Determine High Contrast Brightness:
    // JavaFX Color.getBrightness() returns a value between 0.0 (Black) and 1.0 (White).
    val originalBrightness = backgroundColor.getBrightness

    // Set a high target brightness (e.g., 90%) if the background is dark (Brightness < 0.5).
    // Set a low target brightness (e.g., 10%) if the background is light (Brightness >= 0.5).
    val targetBrightness = if (originalBrightness < 0.5) {
      // Background is dark -> make text bright
      0.9
    } else {
      // Background is light -> make text dark
      0.1
    }

    // 3. Keep the same saturation for color vibrance, or clamp it.
    // Use the original saturation for a more colorful opposite.
    val saturation = backgroundColor.getSaturation

    // Create the final high-contrast color using the adjusted HSB values
    Color.hsb(complementaryHue, saturation, targetBrightness)
  }


  def getBlackOrWhiteContrast(backgroundColor: Color): Color = {
    // Use a standard luminance threshold (often 0.5)
    val threshold = 0.5

    // JavaFX's getBrightness() correlates to the 'B' (Brightness/Value) in HSB.
    // If the background is bright (B >= 0.5), use black text.
    if (backgroundColor.getBrightness >= threshold) {
      Color.BLACK
    } else {
      // If the background is dark (B < 0.5), use white text.
      Color.WHITE
    }
  }

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

  def mkCssTextFill(color: Color): String = {
    "-fx-text-fill: " + ColorUtil.toCssString(color) + ";"
  }


}
