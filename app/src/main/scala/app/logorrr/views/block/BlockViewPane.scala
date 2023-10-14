package app.logorrr.views.block

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control._
import javafx.scene.layout.VBox

import scala.collection.mutable.ListBuffer


class BlockViewPane(selectedLineNumberProperty: SimpleIntegerProperty)
  extends ScrollPane
    with HasBlockSizeProperty
    with CanLog {

  val selectedElemProperty = new SimpleObjectProperty[LogEntry]()

  val filtersProperty = new SimpleListProperty[Filter]()

  private val entriesProperty = new SimpleListProperty[LogEntry](FXCollections.observableArrayList())

  override val blockSizeProperty: SimpleIntegerProperty = new SimpleIntegerProperty()

  /** vertical box which holds BlockViews and serves as a canvas for painting */
  private val vbox = new VBox()

  // listeners
  private val repaintListener: ListChangeListener[LogEntry] = (_: ListChangeListener.Change[_ <: LogEntry]) => {
    // logTrace("repaint entries")
    repaint()
  }

  private val blockSizeListener: InvalidationListener = (observable: Observable) => {
    // logTrace("repaint blocksize")
    repaint()
  }

  private val selectedElemListener =
    JfxUtils.onNew[LogEntry](logEntry => selectedLineNumberProperty.set(logEntry.lineNumber))

  private val widthListener = JfxUtils.onNew[Number](n => {
    // logTrace("repaint width:" + n.intValue())
    repaint()
  })

  init()

  private def init(): Unit = {
    initListeners()

    setContent(vbox)
  }

  private def initListeners(): Unit = {
    selectedElemProperty.addListener(selectedElemListener)
    widthProperty().addListener(widthListener)
    blockSizeProperty.addListener(blockSizeListener)
    entriesProperty.addListener(repaintListener)
  }

  def shutdown(): Unit = {
    // remove listeners
    selectedElemProperty.removeListener(selectedElemListener)
    widthProperty().removeListener(widthListener)
    blockSizeProperty.removeListener(blockSizeListener)
    entriesProperty.removeListener(repaintListener)
  }


  def setCanvasWidth(value: Double): Unit = super.setWidth(value)

  def getEntriesSize(): Int = entriesProperty.size()

  def setEntries(es: ObservableList[LogEntry]): Unit = entriesProperty.setValue(es)

  def repaint(): Unit = {
    if (isVisible) {

      val blockSize = getBlockSize()

      if (blockSize > 0 && getWidth.toInt > 0) {
        // free old memory and listeners otherwise we get a memory leak
        vbox.getChildren.forEach {
          case c: BlockView => c.shutdown()
          case _ =>
        }

        val blockHeight: Int =
          BlockView.calcVirtualHeight(blockSize, blockSize, getWidth.toInt, getEntriesSize())

        val blockViews: Seq[BlockView] = {
          // if virtual canvas height is lower than maxheight, just create one sqView and be done with it
          if (blockHeight <= BlockImage.MaxHeight) {
            val blockView = new BlockView(selectedLineNumberProperty, filtersProperty, blockSizeProperty, widthProperty, selectedElemProperty)
            blockView.setWidth(getWidth.toInt)
            blockView.setHeight(blockHeight)
            blockView.setEntries(entriesProperty)
            Seq(blockView)
          } else {
            // if the virtual canvas height exceeds SQImage.MaxHeight, iterate and create new SQViews
            val nrOfElemsInRow = (getWidth.toInt / blockSizeProperty.get())
            val nrOfRowsPerSquareView = (BlockImage.MaxHeight / blockSizeProperty.get())
            val nrElemsInSqView = nrOfRowsPerSquareView * nrOfElemsInRow
            var curIndex = 0
            val lb = new ListBuffer[BlockView]

            while (curIndex < getEntriesSize()) {
              val v = new BlockView(selectedLineNumberProperty, filtersProperty, blockSizeProperty, widthProperty, selectedElemProperty)
              v.setWidth(getWidth.toInt)
              val end = if (curIndex + nrElemsInSqView < getEntriesSize()) {
                curIndex + nrElemsInSqView
              } else {
                getEntriesSize()
              }
              val blockViewEntries = entriesProperty.subList(curIndex, end)
              v.setEntries(blockViewEntries)
              v.setHeight(BlockView.calcVirtualHeight(blockSize, blockSize, getWidth.toInt, blockViewEntries.size))
              lb.addOne(v)
              curIndex = curIndex + nrElemsInSqView
            }
            lb.toSeq
          }
        }

        vbox.getChildren.setAll(blockViews: _*)
        blockViews.foreach(_.repaint())
      } else {
        logWarn(s"Blocksize: $blockSize, getWidth: $getWidth ")
      }
    } else {
      // logTrace("invisible ...")
    }
    ()
  }


  def scrollToEnd(): Unit = setVvalue(getVmax)


}