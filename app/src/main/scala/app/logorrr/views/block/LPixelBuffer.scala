package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.geometry.Rectangle2D
import javafx.scene.image.{PixelBuffer, PixelFormat}
import javafx.scene.paint.Color

import java.nio.IntBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala

case class LPixelBuffer(width: Int
                        , height: Int
                        , blockSizeProperty: SimpleIntegerProperty
                        , entriesProperty: SimpleListProperty[LogEntry]
                        , filtersProperty: SimpleListProperty[Filter]
                        , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                        , rawInts: Array[Int]) extends {
  private val buffer: IntBuffer = IntBuffer.wrap(rawInts)
} with PixelBuffer[IntBuffer](width, height, buffer, PixelFormat.getIntArgbPreInstance) with CanLog {

  assert(width != 0, s"width was $width.")
  assert(height != 0, s"height was $height.")
  assert(width <= BlockImage.MaxWidth, s"width was $width which exceeds ${BlockImage.MaxWidth}.")
  assert(height <= BlockImage.MaxHeight, s"height was $height which exceeds ${BlockImage.MaxHeight}.")
  assert(height * width > 0)

  lazy val background: Array[Int] = Array.fill(width * height)(ColorUtil.toARGB(Color.AZURE))

  private val roi = new Rectangle2D(0, 0, width, height)

  private def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  def blockSize: Int = blockSizeProperty.get()

  // todo check visibility
  def repaint(ctx: String
              , filters: Seq[Filter]
              , selectedEntry: LogEntry): Unit = timeR({
    if (blockSize != 0) {
      if (Option(filters).isEmpty) {
        logWarn("filters is null")
      } else
        updateBuffer((_: PixelBuffer[IntBuffer]) => {
          cleanBackground()
          var i = 0
          entriesProperty.forEach(e => {
            if (e.equals(selectedEntry)) {
              drawRect(i, Color.YELLOW)
            } else {
              drawRect(i, Filter.calcColor(e.value, filters))
            }
            i = i + 1
          })
          roi
        })
    } else {
      logWarn(s"getBlockWidth() = $blockSize")
    }
  }, s"$ctx repaint")


  /**
   * draws a filled rectangle on the given index
   */
  def draw(index: Int, color: Color): Unit = {
    if (blockSize != 0) {
      repaint(s"draw[$index]", filtersProperty.get().asScala.toSeq, selectedEntryProperty.get())
      updateBuffer((_: PixelBuffer[IntBuffer]) => {
        // drawRect(e.lineNumber - 1, Filter.calcColor(e.value, filtersProperty.asScala.toSeq).darker().darker())
        drawRect(index, color)
        roi
      })
    }
  }

  private def drawRect(i: Int, color: Color): Unit = {
    val nrOfBlocksInX = width / blockSize
    val xPos = (i % nrOfBlocksInX) * blockSize
    val yPos = (i / nrOfBlocksInX) * blockSize
    drawRect(
      color
      , xPos
      , yPos
      , blockSize
      , blockSize
      , width
    )
  }

  private def drawRect(color: Color
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
