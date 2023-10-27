package app.logorrr.views.block

import javafx.geometry.Rectangle2D


/**
 * Often we need width and height in order to paint something, this is a wrapper for those two values
 * alongside with some boundary checks.
 *
 * The boundaries are defined as 0 <= width < [[BlockImage.MaxWidth]] and 0 <= height < [[BlockImage.MaxHeight]]
 *
 * @param width  width of given shape
 * @param height height of given shape.
 */
case class RectangularShape(width: Double, height: Double) extends Rectangle2D(0.0, 0.0, width, height) {
  // checks in order not to overshoot the boundaries of underlying restrictions of the hardware accelerated api
  assert(width <= BlockImage.MaxWidth, s"width was $width which exceeds ${BlockImage.MaxWidth}.")
  assert(height <= BlockImage.MaxHeight, s"height was $height which exceeds ${BlockImage.MaxHeight}.")

  lazy val area = width * height

}
