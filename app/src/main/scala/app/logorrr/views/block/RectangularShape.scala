package app.logorrr.views.block

import javafx.geometry.Rectangle2D


/**
 * Often we need width and height in order to paint something, this is a wrapper for those two values
 * alongside with some boundary checks.
 *
 * The boundaries are defined as 0 <= width < [[ChunkImage.MaxWidth]] and 0 <= height < [[ChunkImage.MaxHeight]]
 *
 * @param width  width of given shape
 * @param height height of given shape.
 */
case class RectangularShape(width: Double, height: Double) extends Rectangle2D(0.0, 0.0, width, height) {
  // checks in order not to overshoot the boundaries of underlying restrictions of the hardware accelerated api
  // assert(width <= ChunkImage.MaxWidth, s"width was $width which exceeds ${ChunkImage.MaxWidth}.")
  // assert(height <= ChunkImage.MaxHeight, s"height was $height which exceeds ${ChunkImage.MaxHeight}.")
  // assert(width > 0)
  // assert(height > 0)

  val widthAsInt = width.toInt
  val heightAsInt = height.toInt

  lazy val size: Int = (widthAsInt * heightAsInt)

  val asString = s"(w/wInt) (h/hInt) area: ($width/$widthAsInt) ($height/$heightAsInt) $size"

}
