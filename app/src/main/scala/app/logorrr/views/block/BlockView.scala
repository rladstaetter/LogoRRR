package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils, MathUtil}
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color

import scala.math.BigDecimal.RoundingMode
import scala.util.Try

object BlockView {

  private val ScrollBarWidth = 17

  private val MinWidth = 200


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
        val decimal1: BigDecimal = MathUtil.roundUp(nrRows)
        val res = decimal1.intValue * blockHeight
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
class BlockView(name: String
                , selectedLineNumberProperty: SimpleIntegerProperty
                , filtersProperty: SimpleListProperty[Filter]
                , blockSizeProperty: SimpleIntegerProperty
                , widthProperty: ReadOnlyDoubleProperty
                , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                , entries: java.util.List[LogEntry]
                , heightProperty: SimpleIntegerProperty) extends ImageView with CanLog {

  setStyle(
    """
      |-fx-padding: 0;
      |-fx-margin: 0;
      |-fx-background-insets: 0;
      | """.stripMargin)
  /*
    private val widthProperty = new SimpleIntegerProperty(outerWidthProperty.get().intValue())

    private val onClickListener: ChangeListener[LogEntry] = JfxUtils.onNew {
      logEntry =>
        selectedElemProperty.set(logEntry)
        val i = entries.indexOf(logEntry)
        if (i >= 0) {
          blockImage.draw(i, Color.YELLOW)
        } else {
          logWarn("onClickListener " + i)
        }
    }

    private val selectedLineNumberListener = new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        if (t.intValue() != t1.intValue()) {
          blockImage.draw(t1.intValue(), Color.YELLOW)
        }
      }
    }


    val selectedEntryProperty: SimpleObjectProperty[LogEntry] = new SimpleObjectProperty[LogEntry]()
  */
  private val blockImage = new BlockImage(name
    , widthProperty
    , blockSizeProperty
    , entries
    , filtersProperty
    , selectedEntryProperty
    , heightProperty
  )
  /*
    private val widthListener = JfxUtils.onNew[Number](n => {
      val scrollPaneWidth = n.intValue()
      if (scrollPaneWidth < BlockImage.MaxWidth) {
        val proposedWidth = scrollPaneWidth - BlockView.ScrollBarWidth
        if (proposedWidth > BlockView.MinWidth) {
          setWidth(proposedWidth)
        } else {
          // logTrace(s"Proposed width ($proposedWidth) < SQView.MinWidth (${BlockView.MinWidth}), not adjusting width of canvas ...")
        }
      } else {
        // logTrace(s"ScrollPaneWidth ($scrollPaneWidth) >= SQImage.MaxWidth (${BlockImage.MaxWidth}), not adjusting width of canvas ...")
      }
    })
    */
  /*
  val mouseEventHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = BlockView.indexOf(me.getX.toInt, me.getY.toInt, blockSizeProperty.get, widthProperty.get)
      getEntryAt(index) match {
        case Some(value) => selectedEntryProperty.set(value)
        case None => System.err.println("no element found")
      }
    }
  }

*/
  init()

  def init(): Unit = {
    // setOnMouseClicked(mouseEventHandler)
    addListener()
    setImage(blockImage)
  }

  def shutdown(): Unit = {
    blockImage.shutdown()
    removeListener()
    setImage(null)
  }

  def addListener(): Unit = {
    // selectedLineNumberProperty.addListener(selectedLineNumberListener)
    //selectedEntryProperty.addListener(onClickListener)
    // widthProperty.addListener(widthListener)
  }

  private def removeListener(): Unit = {
    //selectedLineNumberProperty.removeListener(selectedLineNumberListener)
    //selectedEntryProperty.removeListener(onClickListener)
    //widthProperty.removeListener(widthListener)
  }


  def setWidth(width: Int): Unit = logWarn("nono") //widthProperty.set(width)

  private def getEntryAt(index: Int): Option[LogEntry] = Try(entries.get(index)).toOption


}