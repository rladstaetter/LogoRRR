package app.logorrr.clv

import javafx.geometry.Rectangle2D

object ChunkShape {

  val MaxWidth = 4096
  val MaxHeight = 4096

}

/**
 * Often we need width and height in order to paint something, this is a wrapper for those two values
 * alongside with some boundary checks.
 *
 * The boundaries are defined as 0 <= width < [[ChunkShape.MaxWidth]] and 0 <= height < [[ChunkShape.MaxHeight]]
 *
 * @param width  width of given shape
 * @param height height of given shape.
 */
case class ChunkShape(width: Int, height: Int) extends Rectangle2D(0.0, 0.0, width, height) {
  // checks in order not to overshoot the boundaries of underlying restrictions of the hardware accelerated api
  assert(width <= ChunkShape.MaxWidth, s"width was $width which exceeds ${ChunkShape.MaxWidth}.")
  assert(height <= ChunkShape.MaxHeight, s"height was $height which exceeds ${ChunkShape.MaxHeight}.")
  assert(width > 0)
  assert(height > 0)

  lazy val size: Int = width * height

}
