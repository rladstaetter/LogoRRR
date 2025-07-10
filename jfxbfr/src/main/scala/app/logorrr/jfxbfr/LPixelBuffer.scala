package app.logorrr.jfxbfr

import app.logorrr.model.LogEntry
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.scene.image.{PixelBuffer, PixelFormat}
import javafx.scene.paint.Color
import net.ladstatt.util.log.CanLog

import java.nio.IntBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala


/**
 * Paint directly into a byte array for performant image manipulations.
 */
object LPixelBuffer extends CanLog {

  def paintBlock(pixelBuffer: PixelBuffer[IntBuffer]
                 , color: Color
                 , index: Int
                 , blockSize: Int
                 , width: Double
                 , isSelected: Boolean
                 , isFirstVisible: Boolean
                 , isLastVisible: Boolean
                 , isVisibleInTextView: Boolean): Unit = {
    val blockColor: BlockColor = LPixelBuffer.calcBlockColor(color, isSelected, isFirstVisible, isLastVisible, isVisibleInTextView)
    LPixelBuffer.drawRectangle(pixelBuffer, blockColor, index, blockSize, width)
  }


  def calcBlockColor(color: Color
                     , isSelected: Boolean
                     , isFirstVisible: Boolean
                     , isLastVisible: Boolean
                     , isVisibleInTextView: Boolean): BlockColor = {
    val colbrighter = color.brighter()
    val (c, cd, cb, cbb) = (ColorUtil.toARGB(color), ColorUtil.toARGB(color.darker()), ColorUtil.toARGB(colbrighter), ColorUtil.toARGB(colbrighter.brighter()))
    // mystical color setting routine for setting border colors of viewport and blocks correctly
    val blockColor = {
      (isSelected, isFirstVisible, isLastVisible, isVisibleInTextView) match {
        case (false, false, false, false) => BlockColor(c, cb, cb, cd, cd)
        case (true, false, false, false) => LColors.color0
        case (false, true, false, false) => BlockColor(cb, LColors.yb, LColors.yb, LColors.y, cd)
        case (false, false, false, true) => BlockColor(cb, LColors.yb, cbb, LColors.y, cd)
        case (true, false, false, true) => LColors.color1
        case (false, false, true, false) => BlockColor(cb, LColors.yb, cbb, LColors.y, LColors.y)
        case (true, true, false, false) => LColors.color2
        case (true, false, true, false) => LColors.color2
        case _ => BlockColor(c, cb, cb, cd, cd) // should never happen (?)
      }
    }
    blockColor
  }

  /**
   *
   * @param pixelBuffer IntBuffer containing image information
   * @param index       index (where) should be painted
   * @param width       width of 'canvas' to paint on
   *
   */
  def drawRectangle(pixelBuffer: PixelBuffer[IntBuffer], blockColor: BlockColor, index: Int, blockSize: Int, width: Double): Unit = {
    val nrOfBlocksInX = (width / blockSize).toInt
    val xPos = (index % nrOfBlocksInX) * blockSize
    val yPos = (index / nrOfBlocksInX) * blockSize
    drawRectangle(pixelBuffer
      , blockColor
      , xPos
      , yPos
      , blockSize
      , blockSize
      , width.toInt
    )
  }

  // performance sensitive function
  // using while loops for more performance
  // use benchmark module to perform statistics
  // here are the current results (macbook pro m1)
  // LogoRRRBenchmark.benchmarkDrawRect  thrpt   50  782011,597 ± 605,345  ops/s
  // LogoRRRBenchmark.benchmarkDrawRect  thrpt   50  781747,451 ± 795,479  ops/s
  private def drawRectangle(pixelBuffer: PixelBuffer[IntBuffer]
                            , blockColor: BlockColor
                            , x: Int
                            , y: Int
                            , width: Int
                            , height: Int
                            , canvasWidth: Int): Unit = {
    val rawInts = pixelBuffer.getBuffer.array()
    val maxHeight = y + height - 1
    val length = width - 1
    val squareWidth = length - 1
    val startIdx = y * canvasWidth + x
    val endIdx = maxHeight * canvasWidth + x + length

    // Check if start and end indices are within bounds
    if (startIdx >= 0 && endIdx < rawInts.length) {
      val color = blockColor.color
      val upperBorderCol = blockColor.upperBorderCol
      val bottomBorderCol = blockColor.bottomBorderCol
      val leftBorderCol = blockColor.leftBorderCol
      val rightBorderCol = blockColor.rightBorderCol

      // Update rawInts directly without array copying
      var ly = y
      while (ly < maxHeight) {
        val startPos = ly * canvasWidth + x
        var i = 0
        while (i <= squareWidth) {
          rawInts(startPos + i) = color
          i += 1
        }
        ly += 1
      }

      // Paint highlights & shadows if square is big enough
      if (width >= 2 && height >= 2) {
        // Paint upper border from upper left corner to upper right corner
        var i = 0
        while (i <= squareWidth) {
          rawInts(y * canvasWidth + x + i) = upperBorderCol
          rawInts(maxHeight * canvasWidth + x + i) = bottomBorderCol
          i += 1
        }

        // Calculate x positions for left and right borders
        ly = y
        while (ly < maxHeight) {
          val idx = ly * canvasWidth + x
          rawInts(idx) = leftBorderCol
          rawInts(idx + length) = rightBorderCol
          ly += 1
        }
      }
    } else {
      //      logWarn(s"tried to paint outside allowed index. [endIdx = maxHeight * canvasWidth + x + length] ($endIdx = $maxHeight * $canvasWidth + $x + $length) ")
    }
  }

}


case class LPixelBuffer(blockNumber: Int
                        , shape: RectangularShape
                        , blockSizeProperty: SimpleIntegerProperty
                        , entries: java.util.List[LogEntry]
                        , filtersProperty: ObservableList[_ <: MutFilter[_]]
                        , intBuffer: IntBuffer
                        , selectedLineNumberProperty: SimpleIntegerProperty
                        , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                        , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
                       ) extends
  PixelBuffer[IntBuffer](shape.width.toInt
    , shape.height.toInt
    , intBuffer
    , PixelFormat.getIntArgbPreInstance) {

  init()

  def init(): Unit = {
    paint()
  }


  private def getBlockSize: Int = blockSizeProperty.get()

  def filters: Seq[MutFilter[_]] = Option(filtersProperty).map(_.asScala.toSeq).getOrElse({
    Seq()
  })

  /** function is performance relevant, see corresponding jmh tests in the benchmarks module */
  private def paint(): Unit = {
    if (getBlockSize != 0 && shape.width > getBlockSize) {
      if (getBlockSize == 1) {
        paintPixels()
      } else if (getBlockSize > 1) {
        paintRects()
      }
    }
  }

  // helper functions to determine visibility and status of each entry

  /** returns true if entry is active (= selected) - typically this entry is highlighted in some form */
  private def isSelected(lineNumber: Int): Boolean = lineNumber == selectedLineNumberProperty.getValue

  /** element is the first visible element in the text view (the start of the visible elements) */
  private def isFirstVisible(lineNumber: Int): Boolean = lineNumber == firstVisibleTextCellIndexProperty.get()

  /** element is the last visible element in the text view (the end of the visisible elements) */
  private def isLastVisible(lineNumber: Int): Boolean = lineNumber == lastVisibleTextCellIndexProperty.get()

  /** element is visible in the text view */
  private def isVisibleInTextView(lineNumber: Int): Boolean = {
    firstVisibleTextCellIndexProperty.get() < lineNumber && lineNumber < lastVisibleTextCellIndexProperty.get()
  }

  def isVisible(lineNumber: Int): Boolean = isFirstVisible(lineNumber) || isLastVisible(lineNumber) || isVisibleInTextView(lineNumber)

  /**
   * special handling for blocksize == 1.
   *
   * Some operations are not necessary for this case, compare this with  paintRects(..)
   */
  private def paintPixels(): Unit = {
    updateBuffer((pb: PixelBuffer[IntBuffer]) => {
      var i = 0
      entries.forEach(e => {
        val col =
          (isSelected(e.lineNumber), isVisible(e.lineNumber)) match {
            case (false, false) => ColorUtil.toARGB(ColorUtil.calcColor(e.value, filters))
            case (false, true) => ColorUtil.toARGB(ColorUtil.calcColor(e.value, filters).brighter())
            case (true, false) => LColors.y
            case (true, true) => LColors.yb
          }
        val rawInts = pb.getBuffer.array()
        if (i < rawInts.length) {
          rawInts(i) = col
        }
        i = i + 1
      })
      shape
    })
  }

  // TODO shape is much too large - could be reduced to the exact position
  // of highlighted block by calculating position
  def paintBlockAtIndexWithColor(i: Int, lineNumber: Int, color: Color): Unit = {
    updateBuffer((pb: PixelBuffer[IntBuffer]) => {
      LPixelBuffer.paintBlock(pb
        , color
        , i
        , getBlockSize
        , shape.width
        , isSelected(lineNumber)
        , isFirstVisible(lineNumber)
        , isLastVisible(lineNumber)
        , isVisibleInTextView(lineNumber))
      shape
    })

  }

  private def paintRects(): Unit = {
    updateBuffer((pb: PixelBuffer[IntBuffer]) => {
      var i = 0
      if (!entries.isEmpty) {
        entries.forEach(e => {
          val color = ColorUtil.calcColor(e.value, filters)
          LPixelBuffer.paintBlock(pb
            , color
            , i
            , getBlockSize
            , shape.width
            , isSelected(e.lineNumber)
            , isFirstVisible(e.lineNumber)
            , isLastVisible(e.lineNumber)
            , isVisibleInTextView(e.lineNumber))
          i = i + 1
        })
      }
      shape
    })
  }


}
