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

  private def calcColors(color: Color): (Int, Int, Int) = {
    (ColorUtil.toARGB(color), ColorUtil.toARGB(color.brighter()), ColorUtil.toARGB(color.darker()))
  }

  /**
   *
   * @param rawInts underlying array to manipulate
   * @param index   index (where) should be painted
   * @param width   width of 'canvas' to paint on
   *
   */
  def drawRectangle(rawInts: Array[Int]
                    , index: Int
                    , width: Double
                    , blockSize: Int
                    , blockColor: BlockColor
                   ): Unit = {
    val nrOfBlocksInX = (width / blockSize).toInt
    val xPos = (index % nrOfBlocksInX) * blockSize
    val yPos = (index / nrOfBlocksInX) * blockSize
    drawRectangle(rawInts
      , blockColor
      , xPos
      , yPos
      , blockSize
      , blockSize
      , width.toInt
    )
  }

  private def drawRectangle(rawInts: Array[Int]
                            , blockColor: BlockColor
                            , x: Int
                            , y: Int
                            , width: Int
                            , height: Int
                            , canvasWidth: Int): Unit = {
    val maxHeight = y + height - 1
    val length = width - 1
    val squareWidth = length - 1
    // Calculate start and end indices for updating rawInts
    val startIdx = y * canvasWidth + x
    val endIdx = maxHeight * canvasWidth + x + length

    // Check if start and end indices are within bounds
    if (startIdx >= 0 && endIdx < rawInts.length) {
      // Update rawInts directly without array copying
      for (ly <- y until maxHeight - 1) {
        val startPos = ly * canvasWidth + x
        // Fill the portion of rawInts with lineArray
        for (i <- 0 to squareWidth) rawInts(startPos + i) = blockColor.color
      }

      // paint highlights & shadows if square is big enough
      if ((width >= 2) && (height >= 2)) {
        // upper left corner to upper right corner
        for (i <- 0 to squareWidth) rawInts(y * canvasWidth + x + i) = blockColor.upperBorderCol
        // lower left corner to lower right corner
        for (i <- 0 to squareWidth) rawInts((maxHeight - 1) * canvasWidth + x + i) = blockColor.bottomBorderCol

        // calculate x positions : starting from (y * canvasWidth + x) being the upper left corner,
        // with a step size of canvasWidth we get the coordinates of the left border
        for (ly <- y until maxHeight - 1) {
          // left border
          rawInts(ly * canvasWidth + x) = blockColor.leftBorderCol
          // the same for the right border, but with another offset
          rawInts(ly * canvasWidth + x + length) = blockColor.rightBorderCol
        }

      }
    } else {
//      logWarn(s"tried to paint outside of allowed index. [endIdx = maxHeight * canvasWidth + x + length] ($endIdx = $maxHeight * $canvasWidth + $x + $length) ")
    }
  }
}


case class LPixelBuffer(blockNumber: Int
                        , shape: RectangularShape
                        , blockSizeProperty: SimpleIntegerProperty
                        , entries: java.util.List[LogEntry]
                        , filtersProperty: ObservableList[Filter]
                        , rawInts: Array[Int]
                        , selectedLineNumberProperty: SimpleIntegerProperty
                        , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                        , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
                       ) extends
  PixelBuffer[IntBuffer](shape.width.toInt
    , shape.height.toInt
    , IntBuffer.wrap(rawInts)
    , PixelFormat.getIntArgbPreInstance) with CanLog {

  lazy val background: Array[Int] = Array.fill(shape.size)(LPixelBuffer.defaultBackgroundColor)

  /* hardcoded highlight color */
  private val highlightedColor = Color.YELLOW
  private lazy val (yellow, yellowBright, yellowDark) = LPixelBuffer.calcColors(highlightedColor)
  private lazy val (yellowVisible, yellowBrightVisible, yellowDarkVisible) = LPixelBuffer.calcColors(highlightedColor.brighter())

  init()

  def init(): Unit = {
    paint()
  }

  private def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  def getBlockSize: Int = blockSizeProperty.get()

  def filters: Seq[Filter] = Option(filtersProperty).map(_.asScala.toSeq).getOrElse({
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
  private def isSelected(lineNumber: Int): Boolean = lineNumber == selectedLineNumberProperty.getValue

  private def isFirstVisible(lineNumber: Int): Boolean = lineNumber == firstVisibleTextCellIndexProperty.get()

  private def isLastVisible(lineNumber: Int): Boolean = lineNumber == lastVisibleTextCellIndexProperty.get()

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
    updateBuffer((_: PixelBuffer[IntBuffer]) => {
      cleanBackground()
      var i = 0
      entries.forEach(e => {
        val col =
          (isSelected(e.lineNumber), isVisible(e.lineNumber)) match {
            case (false, false) => ColorUtil.toARGB(Filter.calcColor(e.value, filters))
            case (false, true) => ColorUtil.toARGB(Filter.calcColor(e.value, filters).brighter())
            case (true, false) => yellow
            case (true, true) => yellowVisible
          }

        rawInts(i) = col

        i = i + 1
      })
      shape
    })
  }

  // TODO shape is much too large - could be reduced to the exact position
  // of highlighted block by calculating position
  def paintBlockAtIndexWithColor(i: Int, lineNumber: Int, color: Color): Unit = {
    updateBuffer((_: PixelBuffer[IntBuffer]) => {
      paintBlock(i, lineNumber, color)
      // logInfo(s"Painting index ${i} lineNumber ${lineNumber} color ${color}")
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


  private def paintBlock(index: Int, lineNumber: Int, color: Color): Unit = {
    val brighterColor = color.brighter()
    val (col, d, b) = (ColorUtil.toARGB(color), ColorUtil.toARGB(color.darker()), ColorUtil.toARGB(brighterColor))
    val (vcol, vd, vb) = (ColorUtil.toARGB(brighterColor), ColorUtil.toARGB(brighterColor.darker()), ColorUtil.toARGB(brighterColor.brighter()))

    // mystical color setting routine for setting border colors of viewport and blocks correctly
    val blockColor =
      (isSelected(lineNumber), isFirstVisible(lineNumber), isLastVisible(lineNumber), isVisibleInTextView(lineNumber)) match {
        case (false, false, false, false) => BlockColor(col, d, d, b, b)
        case (false, false, false, true) => BlockColor(vcol, yellow, vd, vb, yellow)
        case (false, false, true, false) => BlockColor(vcol, yellow, yellow, vb, yellow)
        case (false, true, false, false) => BlockColor(vcol, yellow, vd, yellow, yellow)
        case (false, true, true, false) => BlockColor(col, d, d, b, b)
        case (true, false, false, false) => BlockColor(yellow, yellowDark, yellowDark, yellowBright, yellowBright)
        case (true, false, false, true) => BlockColor(yellowVisible, yellow, yellowDarkVisible, yellowBrightVisible, yellow)
        case (true, false, true, false) => BlockColor(yellowVisible, yellow, yellow, yellowBrightVisible, yellow)
        case (true, true, false, false) => BlockColor(yellowVisible, yellow, yellowDarkVisible, yellow, yellow)
        case (true, true, true, false) => BlockColor(yellowVisible, yellow, yellow, yellow, yellow)
        case (_, _, _, _) => BlockColor(yellowVisible, yellow, yellow, yellow, yellow)
      }

    LPixelBuffer.drawRectangle(rawInts
      , index
      , shape.width
      , getBlockSize
      , blockColor
    )
  }
}
