package app.logorrr.views.block

import app.logorrr.util.{CanLog, JfxUtils}
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color

import scala.math.BigDecimal.RoundingMode
import scala.util.Try

object BlockView {

  val ScrollBarWidth = 17

  val MinWidth = 200

  trait E {
    def index : Int
    def color : Color
  }


  def indexOf(x: Int, y: Int, blockWidth: Int, blockViewWidth: Int): Int = y / blockWidth * (blockViewWidth / blockWidth) + x / blockWidth

  /**
   * Calculates overall height of virtual canvas
   *
   * @param blockWidth  width of a block
   * @param blockHeight height of a block
   * @param width       width of canvas
   * @param nrEntries   number of elements
   * @return
   */
  def calcVirtualHeight(blockWidth: Int
                        , blockHeight: Int
                        , width: Int
                        , nrEntries: Int): Int = {
    if (blockHeight == 0 || nrEntries == 0) {
      0
    } else {
      if (width > blockWidth) {
        val elemsPerRow = width.toDouble / blockWidth
        val nrRows = nrEntries.toDouble / elemsPerRow
        val decimal = BigDecimal.double2bigDecimal(nrRows)
        val res = decimal.setScale(0, RoundingMode.UP).intValue * blockHeight
        res
      } else {
        0
      }
    }
  }
}

/**
 * Displays a region with max 4096 x 4096 pixels and as many entries as can fit in this region.
 */
class BlockView[Elem <: BlockView.E] extends ImageView with CanLog {

  private val blockSizeProperty = new SimpleIntegerProperty(5)
  private val widthProperty = new SimpleIntegerProperty()
  private val entriesProperty = new SimpleListProperty[Elem](FXCollections.observableArrayList())
  private val selectedIndexProperty = new SimpleIntegerProperty()

  private val mouseEventHandler: EventHandler[MouseEvent] = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = BlockView.indexOf(me.getX.toInt, me.getY.toInt, blockSizeProperty.get, widthProperty.get)
      getEntryAt(index) match {
        case Some(value) => selectedIndexProperty.set(value.index)
        case None => println("no element found")
      }
    }
  }

  setOnMouseClicked(mouseEventHandler)

  /** holds reference to property */
  var blockImageWidthPropertyHolder: ReadOnlyDoubleProperty = _

  private val blockImage = {
    val bi = new BlockImage[Elem]
    bi.widthProperty.bind(widthProperty)
    bi.blockWidthProperty.bind(blockSizeProperty)
    bi.blockHeightProperty.bind(blockSizeProperty)
    bi.entries.bind(entriesProperty)
    bi
  }

  private val widthListener = JfxUtils.onNew[Number](n => {
    val scrollPaneWidth = n.intValue()
    if (scrollPaneWidth < BlockImage.MaxWidth) {
      val proposedWidth = scrollPaneWidth - BlockView.ScrollBarWidth
      if (proposedWidth > BlockView.MinWidth) {
        setWidth(proposedWidth)
      } else {
        logTrace(s"Proposed width (${proposedWidth}) < SQView.MinWidth (${BlockView.MinWidth}), not adjusting width of canvas ...")
      }
    } else {
      logTrace(s"ScrollPaneWidth (${scrollPaneWidth}) >= SQImage.MaxWidth (${BlockImage.MaxWidth}), not adjusting width of canvas ...")
    }
  })

  imageProperty().bind(blockImage.imageProperty)

  def setHeight(height: Int): Unit = blockImage.setHeight(height)

  def setWidth(width: Int): Unit = widthProperty.set(width)

  def bind(blockSizeProperty: SimpleDoubleProperty
           , squareImageVizWidthProperty: ReadOnlyDoubleProperty
           , selectedIndexProperty: SimpleIntegerProperty): Unit = {
    this.blockSizeProperty.bind(blockSizeProperty)
    this.blockImageWidthPropertyHolder = squareImageVizWidthProperty
    this.blockImageWidthPropertyHolder.addListener(widthListener)
    selectedIndexProperty.bind(this.selectedIndexProperty)
  }

  def unbind(): Unit = this.blockImageWidthPropertyHolder.removeListener(widthListener)

  def setEntries(entries: java.util.List[Elem]): Unit = entriesProperty.setAll(entries)

  def getEntryAt(index: Int): Option[Elem] = Try(entriesProperty.get(index)).toOption

  def redraw(): Unit = blockImage.redraw()

}