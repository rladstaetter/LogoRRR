package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.scene.image.{PixelBuffer, PixelFormat}
import javafx.scene.paint.Color

import java.nio.IntBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala

/**
 * Paint directly into a byte array for performant image manipulations.
 */
object LPixelBuffer extends CanLog {

  private def drawRect(rawInts: Array[Int]
                       , i: Int
                       , width: Int
                       , blockSize: Int
                       , color: Color): Unit = {
    if (width > blockSize) {
      val nrOfBlocksInX = width / blockSize
      //    val nrOfBlocksInX = if (width > blockSize) width / blockSize else blockSize

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
    } else {
      logTrace("")
    }
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

case class Range(start: Int, end: Int)

case class LPixelBuffer(blockNumber: Int
                        , range: Range
                        , shape: RectangularShape
                        , blockSizeProperty: SimpleIntegerProperty
                        , entries: java.util.List[LogEntry]
                        , filtersProperty: ObservableList[Filter]
                        , rawInts: Array[Int]
                        , selectedLineNumberProperty: SimpleIntegerProperty) extends
  PixelBuffer[IntBuffer](shape.width
    , shape.height
    , IntBuffer.wrap(rawInts)
    , PixelFormat.getIntArgbPreInstance) with CanLog {

  private val name = s"${range.start}_${range.end}"
  private val bgColor: Int = ColorUtil.toARGB(Color.WHITE)
  lazy val background: Array[Int] = Array.fill(shape.area)(bgColor)

  init()

  def init(): Unit = {
    assert(shape.width != 0, s"For $name, width was ${shape.width}.")
    assert(shape.height != 0, s"For $name, height was ${shape.height}.")
    assert(shape.height * shape.width > 0)
    paint()
  }

  private def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  def blockSize: Int = blockSizeProperty.get()

  def filters = Option(filtersProperty).map(_.asScala.toSeq).getOrElse(Seq())

  // todo check visibility
  private def paint(): Unit = {
    if (blockSize != 0) {
      updateBuffer((_: PixelBuffer[IntBuffer]) => {
        cleanBackground()
        var i = 0
        entries.forEach(e => {
          if (e.lineNumber.equals(selectedLineNumberProperty.getValue())) {
            LPixelBuffer.drawRect(rawInts, i, shape.width, blockSize, Color.YELLOW)
          } else {
            LPixelBuffer.drawRect(rawInts, i, shape.width, blockSize, Filter.calcColor(e.value, filters))
          }
          i = i + 1
        })
        shape
      })
    }
  }


}
