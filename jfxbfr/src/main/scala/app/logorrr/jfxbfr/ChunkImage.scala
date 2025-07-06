package app.logorrr.jfxbfr

import app.logorrr.model.LogEntry
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList
import javafx.scene.image.WritableImage


/**
 * Displays a Chunk, backed by a PixelBuffer.
 */
object ChunkImage {

  var nr = 0
  val MaxWidth = 4096

  val MaxHeight = 4096

  val DefaultScrollBarWidth = 18
  // width of Scrollbars
  val scrollBarWidthProperty = new SimpleIntegerProperty(DefaultScrollBarWidth)
  def setScrollBarWidth(width : Int): Unit = scrollBarWidthProperty.set(width)
  def getScrollBarWidth: Int = scrollBarWidthProperty.get()

  // assuming we have a grid of rectangles, and x and y give the coordinate of a mouse click
  // this function should return the correct index for the surrounding rectangle
  def indexOf(x: Int, y: Int, blockWidth: Int, blockViewWidth: Int): Int = {
    y / blockWidth * (blockViewWidth / blockWidth) + x / blockWidth
  }


  def apply(chunk: Chunk
            , filtersProperty: ObservableList[_ <: Fltr[_]]
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
            , filtersProperty: ObservableList[_ <: Fltr[_]]
            , blockSizeProperty: SimpleIntegerProperty
            , widthProperty: ReadOnlyDoubleProperty
            , heightProperty: SimpleIntegerProperty
            , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
            , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
           ): ChunkImage = {
    val width = ChunkListView.calcListViewWidth(widthProperty.get())
    val height = heightProperty.get()
    val shape = RectangularShape(width, height)
    val rawInts = Array.fill(shape.size)(LColors.defaultBackgroundColor)

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
