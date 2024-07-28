package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.MathUtil
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList
import javafx.scene.image.WritableImage


/**
 * Displays a Chunk, backed by a Pixelbuffer.
 */
object ChunkImage {

  val MaxWidth = 4096

  val MaxHeight = 4096

  // width of Scrollbars
  val ScrollBarWidth = 18

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

  def apply(chunk: Chunk
            , filtersProperty: ObservableList[Filter]
            , selectedLineNumberProperty: SimpleIntegerProperty
            , widthProperty: ReadOnlyDoubleProperty
            , blockSizeProperty: SimpleIntegerProperty
            , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
            , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
           ): ChunkImage = {

    apply(chunk.number
      , entries = chunk.entries
      , selectedLineNumberProperty
      , filtersProperty
      , blockSizeProperty
      , widthProperty
      , heightProperty = new SimpleIntegerProperty(chunk.height)
      , firstVisibleTextCellIndexProperty
      , lastVisibleTextCellIndexProperty)
  }

  def apply(blockNumber: Int
            , entries: java.util.List[LogEntry]
            , selectedLineNumberProperty: SimpleIntegerProperty
            , filtersProperty: ObservableList[Filter]
            , blockSizeProperty: SimpleIntegerProperty
            , widthProperty: ReadOnlyDoubleProperty
            , heightProperty: SimpleIntegerProperty
            , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
            , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
           ): ChunkImage = {
    val width = ChunkListView.calcListViewWidth(widthProperty.get())
    val height = heightProperty.get()
    val shape = RectangularShape(width, height)
    val rawInts = Array.fill(shape.size)(LPixelBuffer.defaultBackgroundColor)
    val pixelBuffer = LPixelBuffer(blockNumber
      , shape
      , blockSizeProperty
      , entries
      , filtersProperty
      , rawInts
      , selectedLineNumberProperty
      , firstVisibleTextCellIndexProperty
      , lastVisibleTextCellIndexProperty
    )
    new ChunkImage(pixelBuffer)
  }

}


/**
 * Paints BlockView (filled with chunks and various other parameters
 *
 * @param pixelBuffer buffer to use for raw painting
 */
class ChunkImage(val pixelBuffer: LPixelBuffer) extends WritableImage(pixelBuffer)
