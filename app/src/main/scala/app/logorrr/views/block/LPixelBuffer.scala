package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.scene.image.{PixelBuffer, PixelFormat}
import javafx.scene.paint.Color

import java.nio.IntBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala

/**
 * Paint directly into a byte array for performant image manipulations.
 */
// TODO profile this code and compare it to an approach using the 'normal' image drawing facilities to see if
// it is worth the ceremony ... :|
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
                        , shape: RectangularShape
                        , blockSizeProperty: SimpleIntegerProperty
                        , entries: java.util.List[LogEntry]
                        , filtersProperty: SimpleListProperty[Filter]
                        , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                        , rawInts: Array[Int]) extends
  PixelBuffer[IntBuffer](shape.width.toInt
    , shape.height.toInt
    , IntBuffer.wrap(rawInts)
    , PixelFormat.getIntArgbPreInstance) with CanLog {

  getBuffer.clear()

  assert(shape.width != 0, s"width was ${shape.width}.")
  assert(shape.height != 0, s"height was ${shape.height}.")
  assert(shape.height * shape.width > 0)

  private val bgColor: Int = ColorUtil.toARGB(Color.WHITE)
  lazy val background: Array[Int] = Array.fill(shape.area.toInt)(bgColor)

  paint()

  private def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  def blockSize: Int = blockSizeProperty.get()

  def filters = Option(filtersProperty.get()).map(_.asScala.toSeq).getOrElse(Seq())

  // todo check visibility
  def paint(): Unit = timeR({
    if (blockSize != 0) {
      if (Option(filtersProperty).isEmpty) {
        logWarn("filters is null")
      } else
        updateBuffer((_: PixelBuffer[IntBuffer]) => {
          logTrace("Painting " + name + ", width : " + shape.width)
          cleanBackground()
          var i = 0
          entries.forEach(e => {
            if (e.equals(selectedEntryProperty.get())) {
              LPixelBuffer.drawRect(rawInts, i, shape.width.toInt, blockSize, Color.YELLOW)
            } else {
              // LPixelBuffer.drawRect(rawInts, i, width, blockSize, blockColor)
              //LPixelBuffer.drawRect(rawInts, i, shape.width.toInt, blockSize, Filter.calcColor(e.value, filters))
              LPixelBuffer.drawRect(rawInts, i, shape.width.toInt, blockSize, ColorUtil.intToColor(i))
            }
            i = i + 1
          })
          shape
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
        LPixelBuffer.drawRect(rawInts, index, shape.width.toInt, blockSize, color)
        shape
      })
    }
  }

}
