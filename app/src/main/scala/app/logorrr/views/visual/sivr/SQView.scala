package app.logorrr.views.visual.sivr

import app.logorrr.util.{CanLog, JfxUtils}
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty}
import javafx.beans.value.WeakChangeListener
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.FXCollections
import javafx.scene.image.ImageView
import javafx.scene.paint.Color

object SQView {

  val ScrollBarWidth = 17

  val MinWidth = 200

  case class E(color: Color)

  /**
   * Calculates overall height of virtual canvas
   *
   * @param blockWidth  width of a block
   * @param blockHeight height of a block
   * @param width       width of canvas
   * @param nrEntries   number of elements
   * @return
   */
  def calcVirtualCanvasHeight(blockWidth: Int
                              , blockHeight: Int
                              , width: Int
                              , nrEntries: Int): Int = {
    if (blockWidth == 0) {
      0
    } else {
      if (width > blockWidth) {
        val elemsPerRow = width / blockWidth
        val nrRows = nrEntries / elemsPerRow
        nrRows * blockHeight
      } else {
        0
      }
    }
  }
}

class SQView extends ImageView with CanLog {


  /** holds reference to property */
  var squareImageWidthPropertyHolder: ReadOnlyDoubleProperty = _


  def setWidth(width: Int): Unit = {
    widthProperty.set(width)
  }

  private val widthListener = JfxUtils.onNew[Number](n => {
    val scrollPaneWidth = n.intValue()
    if (scrollPaneWidth < SquareImageViz.MaxWidth) {
      val proposedWidth = scrollPaneWidth - SQView.ScrollBarWidth
      if (proposedWidth > SQView.MinWidth) {
        setWidth(proposedWidth)
      } else {
        logTrace(s"Proposed width (${proposedWidth}) < SQView.MinWidth (${SQView.MinWidth}), not adjusting width of canvas ...")
      }
    } else {
      logTrace(s"ScrollPaneWidth (${scrollPaneWidth}) >= SquareImageViz.MaxWidth (${SquareImageViz.MaxWidth}), not adjusting width of canvas ...")
    }
  })


  private val sqImage = new SQImage
  private val blockSizeProperty = new SimpleIntegerProperty(5)
  private val widthProperty = new SimpleIntegerProperty()
  private val entriesProperty = new SimpleListProperty[SQView.E](FXCollections.observableArrayList())

  def bind(blockSizeProperty: SimpleDoubleProperty
           , squareImageVizWidthProperty: ReadOnlyDoubleProperty): Unit = {
    this.blockSizeProperty.bind(blockSizeProperty)
    this.squareImageWidthPropertyHolder = squareImageVizWidthProperty
    this.squareImageWidthPropertyHolder.addListener(widthListener)
  }

  def unbind(): Unit = {
    this.squareImageWidthPropertyHolder.removeListener(widthListener)
  }

  private val recalcVirtualCanvasHeightListener: InvalidationListener = (_: Observable) => {
    val blockSize = blockSizeProperty.get()
    if (blockSize != 0) {
      setVirtualCanvasHeight(
        SQView.calcVirtualCanvasHeight(blockSize
          , blockSize
          , widthProperty.get()
          , entriesProperty.size()))
    } else {
      // println("blocksize was null")
    }
  }

  blockSizeProperty.addListener(recalcVirtualCanvasHeightListener)
  widthProperty.addListener(recalcVirtualCanvasHeightListener)
  entriesProperty.addListener(recalcVirtualCanvasHeightListener)

  private val printVirtualCanvasHeight: InvalidationListener = (_: Observable) => {
    // println("virtual canvas height:" + virtualCanvasHeightProperty.get)
  }

  val virtualCanvasHeightProperty = new SimpleIntegerProperty()
  virtualCanvasHeightProperty.addListener(printVirtualCanvasHeight)

  def setVirtualCanvasHeight(canvasHeight: Int): Unit = virtualCanvasHeightProperty.set(canvasHeight)

  def setEntries(entries: java.util.List[SQView.E]): Unit = entriesProperty.setAll(entries)

  def redraw(): Unit = sqImage.redraw()

  sqImage.heightProperty.bind(virtualCanvasHeightProperty.add(blockSizeProperty))
  sqImage.widthProperty.bind(widthProperty)
  sqImage.blockWidthProperty.bind(blockSizeProperty)
  sqImage.blockHeightProperty.bind(blockSizeProperty)
  sqImage.entries.bind(entriesProperty)

  imageProperty().bind(sqImage.imageProperty)


}