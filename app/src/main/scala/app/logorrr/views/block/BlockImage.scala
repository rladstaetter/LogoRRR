package app.logorrr.views.block

import app.logorrr.util.{CanLog, ColorUtil, JfxUtils}
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.FXCollections
import javafx.geometry.Rectangle2D
import javafx.scene.image.{PixelBuffer, PixelFormat, WritableImage}
import javafx.scene.paint.Color

import java.nio.IntBuffer

object BlockImage {

  /** width is constrained by the maximum texture width which is set to 4096 */
  val MaxWidth = 4096

  /** max height of a single SQView, constrained by maximum texture height (4096) */
  val MaxHeight = 4096

}

class BlockImage[Elem <: BlockView.E] extends CanLog {

  var pixelBuffer: PixelBuffer[IntBuffer] = _
  var intBuffer: IntBuffer = _
  var rawInts: Array[Int] = _
  var background: Array[Int] = _
  var roi: Rectangle2D = _


  private val redrawListener: InvalidationListener = (_: Observable) => repaint()

  val entries = new SimpleListProperty[Elem](FXCollections.observableArrayList())

  /* if blockwidth is changed redraw */
  val blockWidthProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(redrawListener)
    p
  }

  private def getBlockWidth(): Int = blockWidthProperty.get()

  /* if blockheight is changed redraw */
  val blockHeightProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(redrawListener)
    p
  }

  private def getBlockHeight(): Int = blockHeightProperty.get()

  val imageProperty = new SimpleObjectProperty[WritableImage]()

  /**
   * height property is calculated on the fly depending on the blockwidth/blockheight,
   * width of SQImage, number of elements and max size of possible of texture (4096).
   * */
  val heightProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(JfxUtils.onNew[Number](height => resetBackingImage(getWidth(), height.intValue)))
    p
  }

  def setHeight(height: Int): Unit = heightProperty.set(height)

  def getHeight(): Int = heightProperty.get()

  val widthProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(JfxUtils.onNew[Number](_ => repaint()))
    p
  }

  def getWidth(): Int = widthProperty.get()

  private def resetBackingImage(width: Int, height: Int): Unit = {
    assert(width != 0, s"width was ${width}.")
    assert(height != 0, s"height was ${height}.")
    assert(width <= BlockImage.MaxWidth, s"width was ${width} which exceeds ${BlockImage.MaxWidth}.")
    assert(height <= BlockImage.MaxHeight, s"height was ${height} which exceeds ${BlockImage.MaxHeight}.")
    assert(height * width > 0)
    //      println(s"Allocating ${intWidth * intHeight} memory ...")
    Option(this.intBuffer) match {
      case Some(value) =>
        value.clear()
        this.rawInts = null
      case None =>
    }
    val bgColor = Color.WHITE
    //val bgColor = ColorUtil.randColor
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

  def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)


  def repaint(): Unit = {
    Option(pixelBuffer) match {
      case Some(pb) =>
        if (getBlockWidth() != 0) {
          pb.updateBuffer((_: PixelBuffer[IntBuffer]) => {
            cleanBackground()
            var i = 0
            entries.forEach(e => {
              drawRect(i, e.color)
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
