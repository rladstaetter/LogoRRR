package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ColorUtil, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.value.ChangeListener
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters.CollectionHasAsScala

object BlockImage {

  /** width is constrained by the maximum texture width which is set to 4096 */
  val MaxWidth = 4096

  /** max height of a single SQView, constrained by maximum texture height (4096) */
  val MaxHeight = 4096

}


class BlockImage(name: String
                 , widthProperty: SimpleIntegerProperty
                 , blockSizeProperty: SimpleIntegerProperty
                 , entriesProperty: SimpleListProperty[LogEntry]
                 , filtersProperty: SimpleListProperty[Filter]
                 , selectedElemProperty: SimpleObjectProperty[LogEntry]) extends CanLog {

  var lpb: LPixelBuffer = _

  /**
   * height property is calculated on the fly depending on the blockwidth/blockheight,
   * width of BlockImage, number of elements and max size of possible of texture (4096).
   * */
  val heightProperty: SimpleIntegerProperty = new SimpleIntegerProperty()

  val imageProperty = new SimpleObjectProperty[WritableImage]()

  private val heightListener: ChangeListener[Number] = JfxUtils.onNew[Number](height => {
  // logTrace("heightListener " + height)
    resetBackingImage(getWidth, height.intValue)
  })

  private val blockSizeListener: InvalidationListener = (_: Observable) => {
    repaint("blockSizeListener")
  }

  val widthListener: ChangeListener[Number] = JfxUtils.onNew[Number](_ => {
    repaint("widthListener")
  })

  init()

  def init(): Unit = {
    addListener()
  }


  def shutdown(): Unit = {
    removeListener() // remove all listeners first
    lpb = null
    imageProperty.set(null)
  }


  def addListener(): Unit = {
    heightProperty.addListener(heightListener)
    widthProperty.addListener(widthListener)
    blockSizeProperty.addListener(blockSizeListener)
  }

  def removeListener(): Unit = {
    heightProperty.removeListener(heightListener)
    widthProperty.removeListener(widthListener)
    blockSizeProperty.removeListener(blockSizeListener)
  }


  private def resetBackingImage(width: Int, height: Int): Unit = {
    lpb = LPixelBuffer(width
      , height
      , blockSizeProperty
      , entriesProperty
      , filtersProperty
      , selectedElemProperty
      , Array.fill(width * height)(ColorUtil.toARGB(Color.WHITE)))
    this.imageProperty.set(new WritableImage(lpb))
  }

  // todo check visibility
  def repaint(ctx: String): Unit = {
    Option(lpb).foreach(_.repaint(ctx, filtersProperty.asScala.toSeq, selectedElemProperty.get()))
  }

  def setHeight(height: Int): Unit = heightProperty.set(height)

  def getWidth: Int = widthProperty.get()

}
