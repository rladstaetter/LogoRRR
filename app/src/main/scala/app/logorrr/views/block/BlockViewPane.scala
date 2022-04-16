package app.logorrr.views.block

import app.logorrr.util.{CanLog, JfxUtils}
import javafx.beans.property.{SimpleDoubleProperty, SimpleListProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ListChangeListener}
import javafx.scene.control._
import javafx.scene.layout.VBox

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._


class BlockViewPane extends ScrollPane with CanLog {

  /** vertical box which holds single SQViews and serves as a canvas for painting */
  private val vbox = new VBox()

  private val listChangeListener: ListChangeListener[BlockView.E] = (_: ListChangeListener.Change[_ <: BlockView.E]) => redrawBlocks()

  private val entries = {
    val es = new SimpleListProperty[BlockView.E](FXCollections.observableArrayList())
    es.addListener(listChangeListener)
    es
  }

  def setEntries(es: Seq[BlockView.E]): Unit = {
    entries.setAll(es.asJava)
  }

  private val recalcSqViewsListener: InvalidationListener = (_: Observable) => redrawBlocks()

  private val blockSizeProperty = {
    val p = new SimpleDoubleProperty(5)
    p.addListener(recalcSqViewsListener)
    p
  }

  widthProperty().addListener(JfxUtils.onNew[Number](_ => redrawBlocks()))

  def setBlockSize(blockSize: Int): Unit = blockSizeProperty.set(blockSize)

  private def getBlockSize(): Int = blockSizeProperty.get().toInt

  private def mkSQView(): BlockView = {
    val squareView = new BlockView
    squareView.bind(blockSizeProperty, widthProperty)
    squareView
  }


  private def redrawBlocks(): Unit = {
    // unbind old listeners or we have a memory problem
    vbox.getChildren.forEach {
      case c: BlockView => c.unbind()
      case _ =>
    }

    val virtualHeight: Int =
      BlockView.calcVirtualHeight(
        blockSizeProperty.get().toInt
        , blockSizeProperty.get().toInt
        , getWidth.toInt
        , entries.size)

    assert(blockSizeProperty.get().toInt > 0)
    assert(getWidth.toInt > 0, getWidth)

    val blockViews: Seq[BlockView] = {
      // if virtual canvas height is lower than maxheight, just create one sqView and be done with it
      if (virtualHeight <= BlockImage.MaxHeight) {
        val sqView = mkSQView()
        sqView.setWidth(getWidth.toInt)
        sqView.setHeight(virtualHeight)
        sqView.setEntries(entries)
        Seq(sqView)
      } else {
        // if the virtual canvas height exceeds SQImage.MaxHeight, iterate and create new SQViews
        val nrOfElemsInRow = (getWidth.toInt / blockSizeProperty.get()).toInt
        val nrOfRowsPerSquareView = (BlockImage.MaxHeight / blockSizeProperty.get()).toInt
        val nrElemsInSqView = nrOfRowsPerSquareView * nrOfElemsInRow
        var curIndex = 0
        val lb = new ListBuffer[BlockView]

        while (curIndex < entries.size) {
          val v = mkSQView()
          v.setWidth(getWidth.toInt)
          val end = if (curIndex + nrElemsInSqView < entries.size) {
            curIndex + nrElemsInSqView
          } else {
            entries.size
          }
          val entriesInSquareView = entries.subList(curIndex, end)
          v.setEntries(entriesInSquareView)
          v.setHeight(BlockView.calcVirtualHeight(getBlockSize(), getBlockSize(), getWidth.toInt, entriesInSquareView.size))
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


  setContent(vbox)
}