package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.geometry.Rectangle2D
import javafx.scene.image.{PixelBuffer, PixelFormat}
import javafx.scene.paint.Color

import java.nio.IntBuffer

object LPixelBuffer {

  private def drawRect(rawInts: Array[Int]
                       , i: Int
                       , width: Int
                       , blockSize: Int
                       , color: Color): Unit = {

    val nrOfBlocksInX = width / blockSize
    val xPos = (i % nrOfBlocksInX) * blockSize
    val yPos = (i / nrOfBlocksInX) * blockSize
    LPixelBuffer.drawRect(rawInts
      , color
      , xPos
      , yPos
      , blockSize
      , blockSize
      , width
    )
  }

  private def drawRect(rawInts: Array[Int]
                       , color: Color
                       , x: Int
                       , y: Int
                       , width: Int
                       , height: Int
                       , canvasWidth: Int): Unit = {
    val col = ColorUtil.toARGB(color)
    val maxHeight = y + height
    val lineArray = Array.fill(width - 1)(col)
    for (ly <- y until maxHeight - 1) {
      val pos = ly * canvasWidth + x + lineArray.length
      if (pos >= 0 && pos < rawInts.length) {
        System.arraycopy(lineArray, 0, rawInts, ly * canvasWidth + x, lineArray.length)
      } else {
        //   logWarn("out of bounds:" + (ly * canvasWidth + x) + " ly " + ly + ", rawints.length " + rawInts.length + "!")
      }
    }
  }

}

case class LPixelBuffer(name: String
                        , width: Int
                        , height: Int
                        , blockSizeProperty: SimpleIntegerProperty
                        , entries: java.util.List[LogEntry]
                        , filtersProperty: SimpleListProperty[Filter]
                        , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                        , rawInts: Array[Int]) extends {
  private val buffer: IntBuffer = IntBuffer.wrap(rawInts)
} with PixelBuffer[IntBuffer](width, height, buffer, PixelFormat.getIntArgbPreInstance) with CanLog {

  getBuffer.clear()

  assert(width != 0, s"width was $width.")
  assert(height != 0, s"height was $height.")
  assert(height * width > 0)

  // checks in order not to overshoot the boundaries of underlying restrictions of the hardware accelerated api
  assert(width <= BlockImage.MaxWidth, s"width was $width which exceeds ${BlockImage.MaxWidth}.")
  assert(height <= BlockImage.MaxHeight, s"height was $height which exceeds ${BlockImage.Height}.")

  private val blockColor = ColorUtil.randColor

  lazy val background: Array[Int] = Array.fill(width * height)(ColorUtil.toARGB(Color.MAGENTA))

  private val roi = new Rectangle2D(0, 0, width, height)

  paint()

  private def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  def blockSize: Int = blockSizeProperty.get()

  // todo check visibility
  def paint(): Unit = timeR({
    if (blockSize != 0) {
      if (Option(filtersProperty).isEmpty) {
        logWarn("filters is null")
      } else
        updateBuffer((_: PixelBuffer[IntBuffer]) => {
          logTrace("Painting " + name)
          cleanBackground()
          var i = 0
          entries.forEach(e => {
            if (e.equals(selectedEntryProperty.get())) {
              LPixelBuffer.drawRect(rawInts, i, width, blockSize, Color.YELLOW)
            } else {
              LPixelBuffer.drawRect(rawInts, i, width, blockSize, blockColor)
              // drawRect(i, Filter.calcColor(e.value, filters))
            }
            i = i + 1
          })
          roi
        })
    } else {
      logWarn(s"getBlockWidth() = $blockSize")
    }
  }, s"$name repaint")


  /**
   * draws a filled rectangle on the given index
   */
  def draw(index: Int, color: Color): Unit = {
    if (blockSize != 0) {
      paint()
      updateBuffer((_: PixelBuffer[IntBuffer]) => {
        // drawRect(e.lineNumber - 1, Filter.calcColor(e.value, filtersProperty.asScala.toSeq).darker().darker())
        LPixelBuffer.drawRect(rawInts, index, width, blockSize, color)
        roi
      })
    }
  }


}
