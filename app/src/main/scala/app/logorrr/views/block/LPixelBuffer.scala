package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil}
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

  val defaultBackgroundColor: Int = ColorUtil.toARGB(Color.WHITE)

  def drawRect(rawInts: Array[Int]
               , i: Int
               , width: Int
               , blockSize: Int
               , color: Int): Unit = {
    val nrOfBlocksInX = width / blockSize
    val xPos = (i % nrOfBlocksInX) * blockSize
    val yPos = (i / nrOfBlocksInX) * blockSize
    drawRect(rawInts
      , color
      , xPos
      , yPos
      , blockSize
      , blockSize
      , width
    )
  }

  private def drawRect(rawInts: Array[Int],
                       col: Int,
                       x: Int,
                       y: Int,
                       width: Int,
                       height: Int,
                       canvasWidth: Int): Unit = {

    val maxHeight = y + height
    val length = width - 1
    val almostLength = length - 1
    // Calculate start and end indices for updating rawInts
    val startIdx = y * canvasWidth + x
    val endIdx = maxHeight * canvasWidth + x + length

    // Check if start and end indices are within bounds
    if (startIdx >= 0 && endIdx < rawInts.length) {
      // Update rawInts directly without array copying
      for (ly <- y until maxHeight - 1) {
        val startPos = ly * canvasWidth + x
        val endPos = startPos + length
        if (startPos >= 0 && endPos < rawInts.length) {
          // Fill the portion of rawInts with lineArray
          for (i <- 0 to almostLength) rawInts(startPos + i) = col
        }
      }
    }
  }
}


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

  lazy val background: Array[Int] = Array.fill(shape.area)(LPixelBuffer.defaultBackgroundColor)
  private lazy val yellow = ColorUtil.toARGB(Color.YELLOW)

  init()

  def init(): Unit = {
    assert(shape.width != 0, s"For $name, width was ${shape.width}.")
    assert(shape.height != 0, s"For $name, height was ${shape.height}.")
    assert(shape.height * shape.width > 0)
    paint()
  }

  private def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  def blockSize: Int = blockSizeProperty.get()

  def filters: Seq[Filter] = Option(filtersProperty).map(_.asScala.toSeq).getOrElse(Seq())



  // todo check visibility
  private def paint(): Unit = {
    if (blockSize != 0) {
      if (shape.width > blockSize) {
        updateBuffer((_: PixelBuffer[IntBuffer]) => {
          cleanBackground()
          var i = 0
          entries.forEach(e => {
            if (e.lineNumber == selectedLineNumberProperty.getValue) {
              LPixelBuffer.drawRect(rawInts, i, shape.width, blockSize, yellow)
            } else {
              val col = ColorUtil.toARGB(Filter.calcColor(e.value, filters))
              LPixelBuffer.drawRect(rawInts, i, shape.width, blockSize, col)
            }
            i = i + 1
          })
          shape
        })
      }
    }
  }


}
