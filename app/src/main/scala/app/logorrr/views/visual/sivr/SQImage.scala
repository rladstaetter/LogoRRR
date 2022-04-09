package app.logorrr.views.visual.sivr

import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.visual.ColorUtil
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.FXCollections
import javafx.geometry.Rectangle2D
import javafx.scene.image.{PixelBuffer, PixelFormat, WritableImage}
import javafx.scene.paint.Color

import java.nio.IntBuffer

class SQImage extends CanLog {

  logTrace("Instantiating " + Debug.inc())
  var pixelBuffer: PixelBuffer[IntBuffer] = _
  var intBuffer: IntBuffer = _
  var rawInts: Array[Int] = _
  var background: Array[Int] = _
  var roi: Rectangle2D = _

  private val redrawListener: InvalidationListener = (_: Observable) => redraw()

  val entries = new SimpleListProperty[SQView.E](FXCollections.observableArrayList())

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
  val heightProperty = new SimpleIntegerProperty()

  def getHeight(): Int = heightProperty.get()

  heightProperty.addListener(JfxUtils.onNew[Number](height => {
    resetBackingImage(getWidth(), height.intValue)
  }))


  val widthProperty = new SimpleIntegerProperty()

  def getWidth(): Int = widthProperty.get()

  widthProperty.addListener(JfxUtils.onNew[Number](width => {
    redraw()
  }))

  private def resetBackingImage(intWidth: Int, intHeight: Int): Unit = {
    if (intHeight * intWidth > 0) {
      //      println(s"Allocating ${intWidth * intHeight} memory ...")
      Option(this.intBuffer) match {
        case Some(value) =>
          value.clear()
          this.rawInts = null
        case None =>
      }
      val color = ColorUtil.randColor
      val rawInts = Array.fill(intWidth * intHeight)(ColorUtil.toARGB(Color.WHITE))
      val buffer: IntBuffer = IntBuffer.wrap(rawInts)
      //      val buffer: IntBuffer = IntBuffer.allocate(intWidth * intHeight)
      val pixelBuffer = new PixelBuffer[IntBuffer](intWidth, intHeight, buffer, PixelFormat.getIntArgbPreInstance)
      val backingImage = new WritableImage(pixelBuffer)
      this.intBuffer = buffer
      this.rawInts = buffer.array()
      this.background = Array.fill(intWidth * intHeight)(ColorUtil.toARGB(color))
      this.pixelBuffer = pixelBuffer
      this.roi = new Rectangle2D(0, 0, intWidth, intHeight)
      this.imageProperty.set(backingImage)
    } else {
      //println(s"intHeight $intHeight, intWidth $intWidth")
    }
  }

  def cleanBackground(): Unit = System.arraycopy(background, 0, rawInts, 0, background.length)


  def redraw(): Unit = {
    Option(pixelBuffer) match {
      case Some(pb) =>
        pb.updateBuffer((_: PixelBuffer[IntBuffer]) => {
          cleanBackground()
          var i = 0
          entries.forEach(e => {
            drawRect(i, e.color)
            i = i + 1
          })

          roi
        })
      case None =>
      //println("pixelBuffer was null")
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
