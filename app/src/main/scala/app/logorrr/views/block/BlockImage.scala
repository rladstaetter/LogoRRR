package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.MathUtil
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList
import javafx.scene.image.WritableImage



object BlockImage {

  val MaxWidth = 4096

  val MaxHeight = 4096

  // width of Scrollbars
  val ScrollBarWidth = 18

  /** defines how many table cells should be rendered per list height */
  val DefaultBlocksPerPage = 4

  // assuming we have a grid of rectangles, and x and y give the coordinate of a mouse click
  // this function should return the correct index for the surrounding rectangle
  def indexOf(x: Int, y: Int, blockWidth: Int, blockViewWidth: Int): Int = {
    y / blockWidth * (blockViewWidth / blockWidth) + x / blockWidth
  }

  /**
   * Calculates overall height of virtual canvas
   *
   * @param blockWidth  width of a block
   * @param blockHeight height of a block
   * @param width       width of canvas
   * @param nrEntries   number of elements
   * @return
   */
  def calcVirtualHeight(blockWidth: Int
                        , blockHeight: Int
                        , width: Int
                        , nrEntries: Int): Int = {
    if (blockHeight == 0 || nrEntries == 0) {
      0
    } else {
      if (width > blockWidth) {
        val elemsPerRow = width.toDouble / blockWidth
        val nrRows = nrEntries.toDouble / elemsPerRow
        val decimal1: BigDecimal = MathUtil.roundUp(nrRows)
        val res = decimal1.intValue * blockHeight
        res
      } else {
        0
      }
    }
  }

  def apply(blockNumber: Int
            , entries: java.util.List[LogEntry]
            , selectedLineNumberProperty: SimpleIntegerProperty
            , filtersProperty: ObservableList[Filter]
            , blockSizeProperty: SimpleIntegerProperty
            , widthProperty: ReadOnlyDoubleProperty
            , heightProperty: SimpleIntegerProperty
           ): BlockImage = {
    val pixelBuffer = LPixelBuffer(blockNumber
      , Range(entries.get(0).lineNumber, entries.get(entries.size - 1).lineNumber)
      , RectangularShape(if (widthProperty.get().toInt - BlockImage.ScrollBarWidth > 0) widthProperty.get().toInt - BlockImage.ScrollBarWidth else widthProperty.get().toInt, heightProperty.get())
      , blockSizeProperty
      , entries
      , filtersProperty
      , Array.fill(widthProperty.get().toInt * heightProperty.get())(LPixelBuffer.defaultBackgroundColor)
      , selectedLineNumberProperty)
    new BlockImage(pixelBuffer)
  }

}


class BlockImage(val pixelBuffer: LPixelBuffer) extends WritableImage(pixelBuffer)
