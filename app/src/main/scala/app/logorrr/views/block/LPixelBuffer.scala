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

  def calcColors(color: Color): (Int, Int, Int) = {
    (ColorUtil.toARGB(color), ColorUtil.toARGB(color.brighter()), ColorUtil.toARGB(color.darker()))
  }

  def drawRect(rawInts: Array[Int]
               , i: Int
               , width: Int
               , blockSize: Int
               , color: Int
               , darkColor: Int
               , brightColor: Int): Unit = {
    val nrOfBlocksInX = width / blockSize
    val xPos = (i % nrOfBlocksInX) * blockSize
    val yPos = (i / nrOfBlocksInX) * blockSize
    drawSquare(rawInts
      , color
      , darkColor
      , brightColor
      , xPos
      , yPos
      , blockSize
      , blockSize
      , width
    )
  }

  private def drawSquare(rawInts: Array[Int]
                         , col: Int
                         , darkCol: Int
                         , brightCol: Int
                         , x: Int
                         , y: Int
                         , width: Int
                         , height: Int
                         , canvasWidth: Int): Unit = {
    val maxHeight = y + height
    val length = width - 1
    val squarewidth = length - 1
    // Calculate start and end indices for updating rawInts
    val startIdx = y * canvasWidth + x
    val endIdx = maxHeight * canvasWidth + x + length

    // Check if start and end indices are within bounds
    if (startIdx >= 0 && endIdx < rawInts.length) {
      // Update rawInts directly without array copying
      for (ly <- y until maxHeight - 1) {
        val startPos = ly * canvasWidth + x
        // val endPos = startPos + length
        // if (startPos >= 0 && endPos < rawInts.length) {
        // Fill the portion of rawInts with lineArray
        for (i <- 0 to squarewidth) rawInts(startPos + i) = col
        //}
      }

      // paint highlights & shadows if sequare is big enough
      if ((width >= 2) && (height >= 2)) {
        // first highlight: upper left corner to upper right corner
        for (i <- 0 to squarewidth) rawInts(y * canvasWidth + x + i) = brightCol
        // first highlight: lower left corner to lower right corner
        for (i <- 0 to squarewidth) rawInts((maxHeight - 1) * canvasWidth + x + i) = darkCol

        // calculate x positions : starting from (y * canvasWidth + x) being the upper left corner,
        // with a step size of canvasWidth we get the coordinates of the left border
        for (ly <- y until maxHeight - 1) {
          rawInts(ly * canvasWidth + x) = brightCol
          // the same for the right border, but with another offset
          rawInts(ly * canvasWidth + x + length) = darkCol
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

  /* hardcoded highlight color */
  private val highlightedColor = Color.YELLOW
  private lazy val (yellow, yellowBright, yellowDark) = LPixelBuffer.calcColors(highlightedColor)

  init()


  def init(): Unit = {
    assert(shape.width != 0, s"For $name, width was ${shape.width}.")
    assert(shape.height != 0, s"For $name, height was ${shape.height}.")
    assert(shape.height * shape.width > 0)
    paint()
  }

  private def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  def getBlockSize: Int = blockSizeProperty.get()

  def filters: Seq[Filter] = Option(filtersProperty).map(_.asScala.toSeq).getOrElse(Seq())

  /** function is performance relevant */
  private def paint(): Unit = {
    if (getBlockSize != 0 && shape.width > getBlockSize) {
      if (getBlockSize == 1) {
        paintPixels()
      } else if (getBlockSize > 1) {
        paintRects()
      }
    }
  }

  /**
   * if blocksize is only equal to 1, set it pixel by pixel
   */
  private def paintPixels(): Unit = {
    updateBuffer((_: PixelBuffer[IntBuffer]) => {
      cleanBackground()
      var i = 0
      entries.forEach(e => {
        if (e.lineNumber == selectedLineNumberProperty.getValue) {
          rawInts(i) = yellow
        } else {
          val col = ColorUtil.toARGB(Filter.calcColor(e.value, filters))
          rawInts(i) = col
        }
        i = i + 1
      })
      shape
    })
  }

  def paintBlockAtIndexWithColor(i: Int, lineNumber: Int, color: Color): Unit = {
    updateBuffer((_: PixelBuffer[IntBuffer]) => {
      paintBlock(i, lineNumber, color)
      shape
    })
  }

  private def paintRects(): Unit = {
    updateBuffer((_: PixelBuffer[IntBuffer]) => {
      cleanBackground()
      var i = 0
      entries.forEach(e => {
        val color = Filter.calcColor(e.value, filters)
        paintBlock(i, e.lineNumber, color)
        i = i + 1
      })
      shape
    })
  }

  private def paintBlock(i: Int, lineNumber: Int, color: Color): Unit = {
    if (lineNumber == selectedLineNumberProperty.getValue) {
      LPixelBuffer.drawRect(rawInts, i, shape.width, getBlockSize, yellow, yellowBright, yellowDark)
    } else {
      val colorDark = color.darker()
      val colorBright = color.brighter()
      LPixelBuffer.drawRect(rawInts
        , i
        , shape.width
        , getBlockSize
        , ColorUtil.toARGB(color)
        , ColorUtil.toARGB(colorDark)
        , ColorUtil.toARGB(colorBright)
      )
    }
  }
}
