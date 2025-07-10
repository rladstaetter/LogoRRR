package app.logorrr.jfxbfr

import javafx.beans.property.SimpleIntegerProperty


/**
 * Displays a Chunk, backed by a PixelBuffer.
 */
object ChunkImage {

  val MaxWidth = 4096

  val MaxHeight = 4096

  val DefaultScrollBarWidth = 18
  // width of Scrollbars
  val scrollBarWidthProperty = new SimpleIntegerProperty(DefaultScrollBarWidth)

  def setScrollBarWidth(width: Int): Unit = scrollBarWidthProperty.set(width)

  def getScrollBarWidth: Int = scrollBarWidthProperty.get()

  // assuming we have a grid of rectangles, and x and y give the coordinate of a mouse click
  // this function should return the correct index for the surrounding rectangle
  def indexOf(x: Int, y: Int, blockWidth: Int, blockViewWidth: Int): Int = {
    y / blockWidth * (blockViewWidth / blockWidth) + x / blockWidth
  }


}
