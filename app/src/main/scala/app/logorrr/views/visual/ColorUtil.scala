package app.logorrr.views.visual

import javafx.scene.paint.Color

import scala.util.Random

object ColorUtil {


  def toARGB(color: Color): Int =
    (color.getOpacity * 255).toInt << 24 |
      (color.getRed * 255).toInt << 16 |
      (color.getGreen * 255).toInt << 8 |
      (color.getBlue * 255).toInt

  def randColor = Color.color(Random.nextDouble()
    , Random.nextDouble()
    , Random.nextDouble()
  )

  def mkPixelArray(squareWidth: Int, color: Color): Array[Int] = {
    val fillColor = ColorUtil.colVal(color)
    Array.fill(squareWidth * squareWidth)(fillColor)
  }

  /**
   * converts a javafx Color to an int suitable to put into an PixelBuffer Int Array
   * */
  private def colVal(c: Color): Int = {
    val alpha = Math.round(c.getOpacity * 255.0).toInt
    val red = Math.round(c.getRed * 255.0).toInt
    val green = Math.round(c.getGreen * 255.0).toInt
    val blue = Math.round(c.getBlue * 255.0).toInt

    var i = alpha
    i = i << 8
    i = i | red
    i = i << 8
    i = i | green
    i = i << 8
    i = i | blue
    i
  }


}
