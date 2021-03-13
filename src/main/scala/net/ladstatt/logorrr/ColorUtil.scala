package net.ladstatt.logorrr

import javafx.scene.paint.Color

object ColorUtil {

  /**
   * converts a javafx Color to an int suitable to put into an PixelBuffer Int Array
   * */
  def colVal(c: Color): Int = {
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

  def mkPixelArray(squareWidth: Int, color: Color): Array[Int] = {
    val fillColor = ColorUtil.colVal(color)
    Array.fill(squareWidth * squareWidth)(fillColor)
  }

}
