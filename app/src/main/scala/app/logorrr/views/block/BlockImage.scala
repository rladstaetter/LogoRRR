package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.FXCollections
import javafx.geometry.Rectangle2D
import javafx.scene.image.{PixelBuffer, PixelFormat, WritableImage}
import javafx.scene.paint.Color

import java.nio.IntBuffer
import scala.jdk.CollectionConverters.ListHasAsScala

object BlockImage {

  /** width is constrained by the maximum texture width which is set to 4096 */
  val MaxWidth = 4096

  /** max height of a single SQView, constrained by maximum texture height (4096) */
  val MaxHeight = 4096

}

class BlockImage extends CanLog {

  val filtersProperty = new SimpleListProperty[Filter]()

  var pixelBuffer: PixelBuffer[IntBuffer] = _
  var intBuffer: IntBuffer = _
  var rawInts: Array[Int] = _
  var background: Array[Int] = _
  var roi: Rectangle2D = _

  private val redrawListener: InvalidationListener = (_: Observable) => repaint()

  val entries = new SimpleListProperty[LogEntry](FXCollections.observableArrayList())

  /* if blockwidth is changed redraw */
  val blockWidthProperty: SimpleIntegerProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(redrawListener)
    p
  }

  /* if blockheight is changed redraw */
  val blockHeightProperty: SimpleIntegerProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(redrawListener)
    p
  }

  private def getBlockWidth(): Int = blockWidthProperty.get()

  def shutdown() : Unit = {
    clearBackingPixelBuffer()

    // just wipe out everything (?!)
    Option(pixelBuffer).foreach(_.getBuffer.clear())
    Option(intBuffer).foreach(_.clear())
    this.rawInts = null
    this.background = null
    this.intBuffer = null
    this.pixelBuffer = null
    this.roi = null
    imageProperty.set(null)
    removeListener()
  }

  def removeListener(): Unit = {
    blockWidthProperty.removeListener(redrawListener)
    blockHeightProperty.removeListener(redrawListener)
  }

  private def getBlockHeight(): Int = blockHeightProperty.get()

  val imageProperty = new SimpleObjectProperty[WritableImage]()

  /**
   * height property is calculated on the fly depending on the blockwidth/blockheight,
   * width of SQImage, number of elements and max size of possible of texture (4096).
   * */
  val heightProperty: SimpleIntegerProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(JfxUtils.onNew[Number](height => resetBackingImage(getWidth(), height.intValue)))
    p
  }
  val widthProperty: SimpleIntegerProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(JfxUtils.onNew[Number](_ => repaint()))
    p
  }

  def setHeight(height: Int): Unit = heightProperty.set(height)

  def getHeight(): Int = heightProperty.get()

  def getWidth(): Int = widthProperty.get()

  private def resetBackingImage(width: Int, height: Int): Unit = {
    clearBackingPixelBuffer()

    assert(width != 0, s"width was $width.")
    assert(height != 0, s"height was $height.")
    assert(width <= BlockImage.MaxWidth, s"width was $width which exceeds ${BlockImage.MaxWidth}.")
    assert(height <= BlockImage.MaxHeight, s"height was $height which exceeds ${BlockImage.MaxHeight}.")
    assert(height * width > 0)

    val bgColor = Color.WHITE
    val rawInts = Array.fill(width * height)(ColorUtil.toARGB(Color.WHITE))
    val buffer: IntBuffer = IntBuffer.wrap(rawInts)
    val pixelBuffer = new PixelBuffer[IntBuffer](width, height, buffer, PixelFormat.getIntArgbPreInstance)
    val backingImage = new WritableImage(pixelBuffer)
    this.intBuffer = buffer
    this.rawInts = buffer.array()
    this.background = Array.fill(width * height)(ColorUtil.toARGB(bgColor))
    this.pixelBuffer = pixelBuffer
    this.roi = new Rectangle2D(0, 0, width, height)
    this.imageProperty.set(backingImage)
  }

  def clearBackingPixelBuffer(): Unit = {
    Option(this.intBuffer) match {
      case Some(value) =>
        value.clear()

        this.rawInts = null
      case None =>
    }
  }

  def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)

  // todo check visibility
  def repaint(): Unit = {
    Option(pixelBuffer) match {
      case Some(pb) =>
        if (getBlockWidth() != 0) {
          pb.updateBuffer((_: PixelBuffer[IntBuffer]) => {
            cleanBackground()
            var i = 0
            entries.forEach(e => {
              drawRect(i, Filter.calcColor(e.value, filtersProperty.asScala.toSeq))
              i = i + 1
            })
            roi
          })
        } else {
          logWarn(s"getBlockWidth() = ${getBlockWidth()}")
        }
      case None => // logTrace("pixelBuffer was null")
    }
  }

  /**
   * draws a filled rectangle on the given index
   *
   * @param e
   */
  def draw(index: Int, color: Color): Unit = {
    Option(pixelBuffer) foreach {
      pb =>
        if (getBlockWidth() != 0) {
          repaint()
          pb.updateBuffer((_: PixelBuffer[IntBuffer]) => {
            // drawRect(e.lineNumber - 1, Filter.calcColor(e.value, filtersProperty.asScala.toSeq).darker().darker())
            drawRect(index, color)
            roi
          })
        }
    }
  }

  def drawRect(i: Int, color: Color): Unit = {
    val width = getWidth()
    val nrOfBlocksInX = width / getBlockWidth()
    val xPos = (i % nrOfBlocksInX) * getBlockWidth()
    val yPos = (i / nrOfBlocksInX) * getBlockHeight()
    drawRect(
      color
      , xPos
      , yPos
      , getBlockWidth()
      , getBlockHeight()
      , getWidth()
    )
  }

  def drawRect(color: Color
               , x: Int
               , y: Int
               , width: Int
               , height: Int
               , canvasWidth: Int): Unit = {

    val col = ColorUtil.toARGB(color)
    val maxHeight = y + height
    val lineArray = Array.fill(width - 1)(col)
    for (ly <- y until maxHeight - 1) {
      setPixelsAt(ly * canvasWidth + x, lineArray)
    }
  }

  def setPixelsAt(destPos: Int, newPixels: Array[Int]): Unit = {
    if (destPos + newPixels.length < rawInts.length) { // TODO check should not be necessary??
      System.arraycopy(newPixels, 0, rawInts, destPos, newPixels.length)
    }
  }

}
