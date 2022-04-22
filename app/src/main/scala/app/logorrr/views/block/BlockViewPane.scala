package app.logorrr.views.block

import app.logorrr.util.{CanLog, JfxUtils}
import javafx.beans.binding.{Bindings, ObjectBinding}
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control._
import javafx.scene.layout.VBox

import java.util.concurrent.Callable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._


class BlockViewPane[Elem <: BlockView.E]
  extends ScrollPane with CanLog {

  def setCanvasWidth(value: Double): Unit = {
    super.setWidth(value)
  }

  /** vertical box which holds single SQViews and serves as a canvas for painting */
  private val vbox = new VBox()

  private val listChangeListener: ListChangeListener[Elem] = (_: ListChangeListener.Change[_ <: Elem]) => redrawBlocks()

  val selectedElemProperty = {
    val p = new SimpleObjectProperty[Elem]()
    p.addListener(JfxUtils.onNew[Elem](n => {
      logTrace("selected " + n)
    }))
    p
  }

  private val entriesProperty = {
    val es = new SimpleListProperty[Elem](FXCollections.observableArrayList())
    es.addListener(listChangeListener)
    es
  }

  def getEntriesSize(): Int = entriesProperty.size()

  def setEntries(es: ObservableList[Elem]): Unit = {
    entriesProperty.setValue(es)
  }

  private val recalcSqViewsListener: InvalidationListener = (_: Observable) => redrawBlocks()

  private val blockSizeProperty: SimpleDoubleProperty = {
    val p = new SimpleDoubleProperty(5)
    p.addListener(recalcSqViewsListener)
    p
  }

  widthProperty().addListener(JfxUtils.onNew[Number](_ => redrawBlocks()))

  def setBlockSize(blockSize: Int): Unit = blockSizeProperty.set(blockSize)

  def setSelectedElem(elem: Elem): Unit = selectedElemProperty.set(elem)

  private def mkBlockView(): BlockView[Elem] = {
    val blockView = new BlockView[Elem]
    blockView.bind(blockSizeProperty, widthProperty, setSelectedElem)
    blockView
  }


  private def redrawBlocks(): Unit = {
    // unbind old listeners or we have a memory problem
    vbox.getChildren.forEach {
      case c: BlockView[Elem] => c.unbind()
      case _ =>
    }

    val virtualHeight: Int =
      BlockView.calcVirtualHeight(
        getBlockSizeAsInt()
        , getBlockSizeAsInt()
        , getWidth.toInt
        , getEntriesSize())

    assert(getBlockSizeAsInt() > 0)
    assert(getWidth.toInt > 0, getWidth)

    val blockViews: Seq[BlockView[Elem]] = {
      // if virtual canvas height is lower than maxheight, just create one sqView and be done with it
      if (virtualHeight <= BlockImage.MaxHeight) {
        val sqView = mkBlockView()
        sqView.setWidth(getWidth.toInt)
        sqView.setHeight(virtualHeight)
        sqView.setEntries(entriesProperty)
        Seq(sqView)
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
          v.setHeight(BlockView.calcVirtualHeight(getBlockSizeAsInt(), getBlockSizeAsInt(), getWidth.toInt, blockViewEntries.size))
          lb.addOne(v)
          curIndex = curIndex + nrElemsInSqView
        }
        lb.toSeq
      }
    }


    vbox.getChildren.setAll(blockViews: _*)
    logTrace(s"Redraw ${blockViews.size} BlockViews")
    blockViews.foreach(_.redraw())
    ()
  }


  private def getBlockSizeAsInt() = blockSizeProperty.get().toInt

  setContent(vbox)
}