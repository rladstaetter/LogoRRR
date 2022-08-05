package app.logorrr.views.block

import app.logorrr.util.{CanLog, JfxUtils}
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

import scala.collection.mutable.ListBuffer




class BlockViewPane[Elem <: BlockView.E]
  extends ScrollPane
    with HasBlockSizeProperty
    with CanLog {

  override val blockSizeProperty: SimpleIntegerProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(_ => repaint())
    p
  }

  def setCanvasWidth(value: Double): Unit = {
    super.setWidth(value)
  }

  /** vertical box which holds single SQViews and serves as a canvas for painting */
  private val vbox = new VBox()
  private val repaintListener: ListChangeListener[Elem] = (_: ListChangeListener.Change[_ <: Elem]) => repaint()

  val selectedElemProperty = {
    val sp = new SimpleObjectProperty[Elem]()
    sp
  }


  private val entriesProperty = {
    val es = new SimpleListProperty[Elem](FXCollections.observableArrayList())
    es.addListener(repaintListener)
    es
  }

  def getEntriesSize(): Int = entriesProperty.size()

  def setEntries(es: ObservableList[Elem]): Unit = {
    entriesProperty.setValue(es)
  }

  widthProperty().addListener(JfxUtils.onNew[Number](_ => repaint()))

  def setSelectedElem(elem: Elem): Unit = selectedElemProperty.set(elem)

  private def mkBlockView(): BlockView[Elem] = {
    val blockView = new BlockView[Elem]
    blockView.bind(blockSizeProperty, widthProperty, setSelectedElem)
    blockView
  }

  def repaint(): Unit = {
    if (isVisible) {
      // unbind old listeners or we have a memory problem
      vbox.getChildren.forEach {
        case c: BlockView[Elem] => c.unbind()
        case _ =>
      }

      val virtualHeight: Int =
        BlockView.calcVirtualHeight(
          getBlockSize()
          , getBlockSize()
          , getWidth.toInt
          , getEntriesSize())
      /*
        assert(getBlockSize() > 0)
        assert(getWidth.toInt > 0, getWidth)
        */
      if (getBlockSize() > 0 && getWidth.toInt > 0) {
        val blockViews: Seq[BlockView[Elem]] = {
          // if virtual canvas height is lower than maxheight, just create one sqView and be done with it
          if (virtualHeight <= BlockImage.MaxHeight) {
            val blockView = mkBlockView()
            blockView.setWidth(getWidth.toInt)
            blockView.setHeight(virtualHeight)
            blockView.setEntries(entriesProperty)
            Seq(blockView)
          } else {
            // if the virtual canvas height exceeds SQImage.MaxHeight, iterate and create new SQViews
            val nrOfElemsInRow = (getWidth.toInt / blockSizeProperty.get()).toInt
            val nrOfRowsPerSquareView = (BlockImage.MaxHeight / blockSizeProperty.get()).toInt
            val nrElemsInSqView = nrOfRowsPerSquareView * nrOfElemsInRow
            var curIndex = 0
            val lb = new ListBuffer[BlockView[Elem]]

            while (curIndex < getEntriesSize()) {
              val v = mkBlockView()
              v.setWidth(getWidth.toInt)
              val end = if (curIndex + nrElemsInSqView < getEntriesSize()) {
                curIndex + nrElemsInSqView
              } else {
                getEntriesSize()
              }
              val blockViewEntries = entriesProperty.subList(curIndex, end)
              v.setEntries(blockViewEntries)
              v.setHeight(BlockView.calcVirtualHeight(getBlockSize(), getBlockSize(), getWidth.toInt, blockViewEntries.size))
              lb.addOne(v)
              curIndex = curIndex + nrElemsInSqView
            }
            lb.toSeq
          }
        }

        vbox.getChildren.setAll(blockViews: _*)
        // logTrace(s"Redraw ${blockViews.size} BlockViews")
        blockViews.foreach(_.repaint())
      } else {
        logWarn(s"Blocksize: ${getBlockSize()}, getWidth: ${getWidth} ")
      }
    } else {
      logTrace("invisible ...")
    }
    ()
  }

  setContent(vbox)

}