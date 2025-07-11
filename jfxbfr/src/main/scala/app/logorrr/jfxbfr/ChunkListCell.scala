package app.logorrr.jfxbfr

import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.scene.control.ListCell
import javafx.scene.image.{ImageView, PixelBuffer, PixelFormat, WritableImage}
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.paint.Color
import net.ladstatt.util.log.CanLog

import java.nio.IntBuffer
import scala.util.Try


/**
 * Paint directly into a byte array for performant image manipulations.
 */
object ChunkListCell extends CanLog {

  def paintBlock(pixelBuffer: PixelBuffer[IntBuffer]
                 , index: Int
                 , color: Color
                 , blockSize: Int
                 , width: Double
                 , isSelected: Boolean
                 , isFirstVisible: Boolean
                 , isLastVisible: Boolean
                 , isVisibleInTextView: Boolean): Unit = {
    val blockColor: BlockColor = ChunkListCell.calcBlockColor(color, isSelected, isFirstVisible, isLastVisible, isVisibleInTextView)
    ChunkListCell.drawRectangle(pixelBuffer, index, blockColor, blockSize, width)
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
  def drawRectangle(pixelBuffer: PixelBuffer[IntBuffer]
                    , index: Int
                    , blockColor: BlockColor
                    , blockSize: Int
                    , width: Double): Unit = {
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



/**
 * A listcell which can contain one or more log entries.
 *
 * To see how those cells are populated, see [[Chunk.mkChunks]]. [[ChunkImage]] is responsible to draw all Chunks
 */
class ChunkListCell[A](widthProperty: ReadOnlyDoubleProperty
                    , blockSizeProperty: SimpleIntegerProperty
                    , scrollTo: A => Unit
                    , logEntryVizor: Vizor[A]
                    , logEntryChozzer: ColorChozzer[A]
                    , elementSelector: ElementSelector[A]
                   ) extends ListCell[Chunk[A]] with CanLog {

  val view = new ImageView()

  /**
   * @param maxOccupiedWidth max space in x direction where blocks will be shown
   * @param me               mouse event
   * @return -1 if MouseEvent is outside the region of interest
   */
  private def calcIndex(maxOccupiedWidth: Double, me: MouseEvent): Int = {
    if (me.getX <= maxOccupiedWidth) {
      ChunkImage.indexOf(me.getX.toInt, me.getY.toInt, blockSizeProperty.get, ChunkListView.calcListViewWidth(widthProperty.get()).toInt)
    } else -1
  }

  // if user selects an entry in the ChunkListView set selectedLineNumberProperty. This property is observed
  // via an listener and a yellow square will be painted.
  private val mouseClickedHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      // on left mouse button
      if (me.getButton.equals(MouseButton.PRIMARY)) {
        Option(getItem).map(_.cols) match {
          case Some(cols) =>
            val index = calcIndex(cols * blockSizeProperty.get(), me)
            getEntryAt(getItem, index) match {
              case Some(value) =>
                elementSelector.select(value)
                // we have to select also the entry in the LogTextView, couldn't get it to work with bindings / listeners
                scrollTo(value)
              case None =>
            }
          case None => // outside of a chunk
        }
      }
    }
  }


  // see #262 - until this is fixed don't activate the mousemoved handler
  /*
   private lazy val mouseMovedHandler: EventHandler[MouseEvent] = (me: MouseEvent) => {
     Option(getItem).map(_.cols) match {
       case Some(cols) =>
         val index = calcIndex(cols * blockSizeProperty.get(), me)
         getEntryAt(getItem, index) match {
           case Some(logEntry) =>
             Option(getGraphic).map(_.asInstanceOf[ImageView].getImage.asInstanceOf[WritableImage].pixelBuffer) match {
               case Some(pb) =>
                 val col = ColorUtil.calcColor(logEntry.value, pb.filters)
                 pb.paintBlockAtIndexWithColor(index, logEntry.lineNumber, col.darker())
                 // schedule repaint with original color again some time in the future
                 val task: Runnable = () => pb.paintBlockAtIndexWithColor(index, logEntry.lineNumber, col)

                 // Create a Timeline that fires once after 250 milliseconds
                 val timeline = new Timeline(new KeyFrame(Duration.millis(250), (_: ActionEvent) => task.run()))
                 timeline.setCycleCount(1) // Ensure it runs only once
                 timeline.play()
               case None =>
             }
           case None => // if no valid item found, ignore
         }
       case None => // if outside of a chunk, just ignore
     }
   }

   if (false) {
     setOnMouseMoved(mouseMovedHandler)
   }

    */
  setOnMouseClicked(mouseClickedHandler)

  override def updateItem(chunk: Chunk[A], empty: Boolean): Unit = JfxUtils.execOnUiThread {
    super.updateItem(chunk, empty)

    if (empty || Option(chunk).isEmpty || blockSizeProperty.get() <= 0 || widthProperty.get() <= 0) {
      setGraphic(null)
    } else {
      val width = ChunkListView.calcListViewWidth(widthProperty.get())
      val shape = RectangularShape(width, chunk.height)

      val pbf = new PixelBuffer[IntBuffer](width.toInt
        , chunk.height
        , IntBuffer.wrap(Array.fill(shape.size)(LColors.defaultBackgroundColor))
        , PixelFormat.getIntArgbPreInstance)

      update(pbf, shape, chunk.entries, blockSizeProperty.get())
      view.setImage(new WritableImage(pbf))
      setGraphic(view)
    }
  }

  def update(pixelBuffer: PixelBuffer[IntBuffer]
             , shape: RectangularShape
             , entries: java.util.List[A]
             , blockSize: Int
            ): Unit = {
    if (blockSize != 0 && shape.width > blockSize) {
      if (blockSize > 1) {
        paintRects(pixelBuffer, entries, shape, blockSize)
      } else {
        paintPixels(pixelBuffer, entries, shape)
      }
    }
  }


  private def getEntryAt(chunk: Chunk[A], index: Int): Option[A] = Try(chunk.entries.get(index)).toOption

  private def paintPixels(pixelBuffer: PixelBuffer[IntBuffer], entries: java.util.List[A], shape: RectangularShape): Unit = pixelBuffer.updateBuffer(updatePixels(entries, shape))

  private def paintRects(pixelBuffer: PixelBuffer[IntBuffer], entries: java.util.List[A], shape: RectangularShape, blockSize: Int): Unit = pixelBuffer.updateBuffer(updateRects(entries, shape, blockSize))

  def updateRects(entries: java.util.List[A], shape: RectangularShape, blockSize: Int)(pb: PixelBuffer[IntBuffer]): Rectangle2D = {
    var i = 0
    if (!entries.isEmpty) {
      entries.forEach(e => {
        ChunkListCell.paintBlock(pb, i, logEntryChozzer.calc(e), blockSize, shape.width, logEntryVizor.isSelected(e), logEntryVizor.isFirstVisible(e), logEntryVizor.isLastVisible(e), logEntryVizor.isVisibleInTextView(e))
        i = i + 1
      })
    }
    shape
  }

  def updatePixels(entries: java.util.List[A], shape: Rectangle2D)(pb: PixelBuffer[IntBuffer]): Rectangle2D = {
    val rawInts = pb.getBuffer.array()
    var i = 0
    entries.forEach(e => {
      val col =
        (logEntryVizor.isSelected(e), logEntryVizor.isVisible(e)) match {
          case (false, false) => ColorUtil.toARGB(logEntryChozzer.calc(e))
          case (false, true) => ColorUtil.toARGB(logEntryChozzer.calc(e).brighter())
          case (true, false) => LColors.y
          case (true, true) => LColors.yb
        }
      if (i < rawInts.length) {
        rawInts(i) = col
      }
      i = i + 1
    })
    shape
  }

}
